package de.gruen.ae2categories.mixin;

import appeng.client.gui.me.common.*;
import de.gruen.ae2categories.*;
import de.gruen.ae2categories.client.ClientCategoryState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = MEStorageScreen.class, remap = false)
public abstract class StorageScreenMixin implements HeaderInteraction {
    @Shadow @Final protected Repo repo;
    @Unique private int categories$pressedButton = -1;

    @Inject(method = "updateBeforeRender", at = @At("HEAD"))
    private void categories$bindNetwork(CallbackInfo ci) {
        ClientCategoryState.bind(repo, ((MEStorageScreen<?>) (Object) this).getMenu());
    }

    @Override public boolean categories$headerGesture() { return categories$pressedButton != -1; }
    @Override public boolean categories$releaseHeader(int button) {
        if (categories$pressedButton != button) return false;
        categories$pressedButton = -1;
        return true;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void categories$clickHeader(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var screen = (MEStorageScreen<?>) (Object) this;
        if (!screen.getMenu().getLinkStatus().connected()) return;
        double x = mouseX - screen.getGuiLeft();
        double y = mouseY - screen.getGuiTop();
        for (Slot slot : screen.getMenu().slots) {
            if (!(slot instanceof RepoSlot rs) || rs.getRepoViewIndex() % repo.getRowSize() != 0) continue;
            String title = ((CategoryView) repo).categories$header(rs.getRepoViewIndex());
            if (title != null && x >= slot.x - 1 && x < slot.x + repo.getRowSize() * 18 - 1
                    && y >= slot.y - 1 && y < slot.y + 17) {
                categories$pressedButton = button;
                if (button == 0) ((CategoryView) repo).categories$toggle(title);
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Redirect(method = "updateSearch", at = @At(value = "INVOKE", target = "Lappeng/client/gui/me/common/Repo;size()I"))
    private int categories$realItemCount(Repo inventory) {
        return ((CategoryView) inventory).categories$itemCount();
    }

    @Inject(method = "drawFG", at = @At("TAIL"))
    private void categories$draw(GuiGraphics graphics, int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        var screen = (MEStorageScreen<?>) (Object) this;
        if (!screen.getMenu().getLinkStatus().connected()) return;
        for (Slot slot : screen.getMenu().slots) {
            if (!(slot instanceof RepoSlot rs) || rs.getRepoViewIndex() % repo.getRowSize() != 0) continue;
            String title = ((CategoryView) repo).categories$header(rs.getRepoViewIndex());
            if (title == null) continue;
            boolean hovered = mouseX - offsetX >= slot.x - 1 && mouseX - offsetX < slot.x + repo.getRowSize() * 18 - 1
                    && mouseY - offsetY >= slot.y - 1 && mouseY - offsetY < slot.y + 17;
            graphics.fill(slot.x - 1, slot.y - 1, slot.x + repo.getRowSize() * 18 - 1, slot.y + 17, hovered ? 0xFF3A4858 : 0xFF29333D);
            String marker = ((CategoryView) repo).categories$isCollapsed(title) ? "+ " : "- ";
            String label = Minecraft.getInstance().font.plainSubstrByWidth(marker + ClientCategoryState.label(repo, title), repo.getRowSize() * 18 - 8);
            graphics.drawString(Minecraft.getInstance().font, label, slot.x + 4, slot.y + 4, 0xFFE5EDF5, false);
        }
    }

    @Inject(method = "renderTooltip", at = @At("TAIL"))
    private void categories$headerTooltip(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        var screen = (MEStorageScreen<?>) (Object) this;
        if (!screen.getMenu().getLinkStatus().connected()) return;
        double x = mouseX - screen.getGuiLeft(), y = mouseY - screen.getGuiTop();
        for (Slot slot : screen.getMenu().slots) {
            if (!(slot instanceof RepoSlot rs) || rs.getRepoViewIndex() % repo.getRowSize() != 0) continue;
            String id = ((CategoryView) repo).categories$header(rs.getRepoViewIndex());
            if (id != null && x >= slot.x - 1 && x < slot.x + repo.getRowSize() * 18 - 1 && y >= slot.y - 1 && y < slot.y + 17) {
                String action = ((CategoryView) repo).categories$isCollapsed(id) ? "Linksklick: ausklappen" : "Linksklick: einklappen";
                graphics.renderComponentTooltip(Minecraft.getInstance().font, java.util.List.of(
                        Component.literal(ClientCategoryState.label(repo, id)), Component.literal(action)), mouseX, mouseY);
                return;
            }
        }
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void categories$blockHeaderClick(Slot slot, int slotIdx, int button, ClickType type, CallbackInfo ci) {
        if (slot instanceof RepoSlot rs && ((CategoryView) repo).categories$header(rs.getRepoViewIndex()) != null) ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void categories$toggle(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_F8) {
            CategoriesMod.enabled = !CategoriesMod.enabled;
            repo.updateView();
            cir.setReturnValue(true);
        }
    }
}
