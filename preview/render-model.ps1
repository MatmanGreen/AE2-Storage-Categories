$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
Add-Type -ReferencedAssemblies System.Drawing.Common,System.Drawing.Primitives -TypeDefinition @'
using System;
using System.Drawing;
public class ModelRaster {
  readonly float[] depth = new float[720*620];
  public ModelRaster(){ Array.Fill(depth,float.NegativeInfinity); }
  public void Face(Bitmap canvas, Bitmap texture, float[] vertices, float shade){
    var x=new float[4];var y=new float[4];var d=new float[4];
    for(int i=0;i<4;i++){float a=vertices[i*3],b=vertices[i*3+1],c=vertices[i*3+2];x[i]=360+(a-c)*13;y[i]=345+(a+c)*6.5f-b*13;d[i]=a+b+c;}
    float[] u={0,1,1,0},v={0,0,1,1};
    for(int t=0;t<2;t++){
      int a=0,b=t+1,c=t+2;
      float det=(y[b]-y[c])*(x[a]-x[c])+(x[c]-x[b])*(y[a]-y[c]);
      if(Math.Abs(det)<0.0001)continue;
      int minX=Math.Max(0,(int)Math.Floor(Math.Min(x[a],Math.Min(x[b],x[c]))));
      int maxX=Math.Min(719,(int)Math.Ceiling(Math.Max(x[a],Math.Max(x[b],x[c]))));
      int minY=Math.Max(0,(int)Math.Floor(Math.Min(y[a],Math.Min(y[b],y[c]))));
      int maxY=Math.Min(619,(int)Math.Ceiling(Math.Max(y[a],Math.Max(y[b],y[c]))));
      for(int py=minY;py<=maxY;py++)for(int px=minX;px<=maxX;px++){
        float wa=((y[b]-y[c])*(px+.5f-x[c])+(x[c]-x[b])*(py+.5f-y[c]))/det;
        float wb=((y[c]-y[a])*(px+.5f-x[c])+(x[a]-x[c])*(py+.5f-y[c]))/det;
        float wc=1-wa-wb;if(wa<-.0001||wb<-.0001||wc<-.0001)continue;
        float z=wa*d[a]+wb*d[b]+wc*d[c];int idx=py*720+px;if(z<depth[idx])continue;
        int tx=Math.Clamp((int)((wa*u[a]+wb*u[b]+wc*u[c])*texture.Width),0,texture.Width-1);
        int ty=Math.Clamp((int)((wa*v[a]+wb*v[b]+wc*v[c])*texture.Width),0,texture.Width-1);
        var color=texture.GetPixel(tx,ty);if(color.A==0)continue;
        canvas.SetPixel(px,py,Color.FromArgb(color.A,(int)(color.R*shade),(int)(color.G*shade),(int)(color.B*shade)));depth[idx]=z;
      }
    }
  }
}
'@
$root = Split-Path $PSScriptRoot -Parent
$model = Get-Content -Raw "$root/src/main/resources/assets/ae2categories/models/block/category_controller.json" | ConvertFrom-Json
$textures = @{}
foreach ($name in @('side','top','bottom')) { $textures[$name] = [Drawing.Bitmap]::new("$root/src/main/resources/assets/ae2categories/textures/block/category_controller_$name.png") }
$bitmap = [Drawing.Bitmap]::new(720,620)
$g = [Drawing.Graphics]::FromImage($bitmap)
$g.Clear([Drawing.ColorTranslator]::FromHtml('#151c26'))
$g.Dispose()
$raster=[ModelRaster]::new()
$g = [Drawing.Graphics]::FromImage($bitmap)
$g.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$g.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::Half
function Point($v) { return [Drawing.PointF]::new([single](360+($v[0]-$v[2])*15),[single](375+($v[0]+$v[2])*7.5-$v[1]*15)) }
$faces = @()
foreach ($e in $model.elements) {
    $x,$y,$z = $e.from
    $xx,$yy,$zz = $e.to
    $polys = @(
        @{p=@(@($x,$yy,$z),@($xx,$yy,$z),@($xx,$yy,$zz),@($x,$yy,$zz)); shade=1.0; side='up'},
        @{p=@(@($xx,$yy,$z),@($xx,$yy,$zz),@($xx,$y,$zz),@($xx,$y,$z)); shade=0.66; side='east'},
        @{p=@(@($x,$yy,$zz),@($xx,$yy,$zz),@($xx,$y,$zz),@($x,$y,$zz)); shade=0.84; side='south'}
    )
    foreach($poly in $polys) {
        $depth=0
        foreach($v in $poly.p){$depth+=$v[0]+$v[1]+$v[2]}
        $faces += @{p=$poly.p; depth=$depth/4; texture=$e.faces.($poly.side).texture.Substring(1); shade=$poly.shade}
    }
}
$g.Dispose()
foreach($face in $faces) {
    $coords=[single[]]@($face.p | ForEach-Object { $_[0]; $_[1]; $_[2] })
    $raster.Face($bitmap,$textures[$face.texture],$coords,[single]$face.shade)
}
$g=[Drawing.Graphics]::FromImage($bitmap)
$font=[Drawing.Font]::new('Segoe UI',20)
$small=[Drawing.Font]::new('Segoe UI',11)
$g.DrawString('ME Category Controller - 0.5',$font,[Drawing.Brushes]::White,35,25)
$g.DrawString('Neue Texturen im AE2-Stil; gerenderte Vorschau, kein Ingame-Screenshot.',$small,[Drawing.Brushes]::LightGray,35,575)
$bitmap.Save("$PSScriptRoot/controller-0.5.png",[Drawing.Imaging.ImageFormat]::Png)
$font.Dispose(); $small.Dispose(); $g.Dispose(); $bitmap.Dispose()
foreach($texture in $textures.Values){$texture.Dispose()}

