package de.gruen.ae2categories.client;

import de.gruen.ae2categories.controller.CategoryControllerMenu;
import de.gruen.ae2categories.network.ConfigurePayload;
import de.gruen.ae2categories.ControllerInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public final class CategoryControllerScreen extends AbstractContainerScreen<CategoryControllerMenu> {
    private EditBox nameField;
    private EditBox priorityField;
    private Button saveButton;
    public CategoryControllerScreen(CategoryControllerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176; imageHeight = 208;
        inventoryLabelY = 115;
    }
    @Override protected void init() {
        String name = nameField == null ? menu.initialName : nameField.getValue();
        String priority = priorityField == null ? Integer.toString(menu.initialPriority) : priorityField.getValue();
        super.init();
        nameField = addRenderableWidget(new EditBox(font, leftPos + 8, topPos + 28, 160, 18, Component.literal("Kategoriename")));
        nameField.setMaxLength(64); nameField.setValue(name);
        nameField.setTooltip(Tooltip.create(Component.literal("Name der Kategorie. Leere Namen werden zu Kategorie.")));
        priorityField = addRenderableWidget(new EditBox(font, leftPos + 65, topPos + 53, 38, 18, Component.literal("Priorität")));
        priorityField.setMaxLength(8);
        priorityField.setFilter(text -> text.matches("-?\\d{0,7}"));
        priorityField.setValue(priority);
        priorityField.setTooltip(Tooltip.create(Component.literal("-1000000 bis 1000000. Höhere Werte stehen oben und gewinnen bei mehrfach zugeordneten Items.")));
        saveButton = addRenderableWidget(Button.builder(Component.literal("Speichern"), button -> save())
                .bounds(leftPos + 108, topPos + 52, 60, 20).build());
        priorityField.setResponder(text -> validatePriority());
        validatePriority();
    }
    private void validatePriority() {
        boolean valid = ControllerInput.priority(priorityField.getValue()).isPresent();
        saveButton.active = valid;
        priorityField.setTextColor(valid ? 0xE0E0E0 : 0xFF7777);
    }
    private void save() {
        var priority = ControllerInput.priority(priorityField.getValue());
        if (priority.isPresent()) PacketDistributor.sendToServer(new ConfigurePayload(menu.containerId, nameField.getValue(), priority.getAsInt()));
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF222C37);
        graphics.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xFF344251);
        for (var slot : menu.slots) {
            graphics.fill(leftPos + slot.x - 1, topPos + slot.y - 1, leftPos + slot.x + 17, topPos + slot.y + 17, 0xFF131B23);
            graphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0xFF708092);
        }
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, "ME Category Controller", 8, 7, 0xFFFFFF, false);
        graphics.drawString(font, "Kategoriename", 8, 18, 0xD8E1ED, false);
        graphics.drawString(font, "Priorität", 8, 58, 0xD8E1ED, false);
        graphics.drawString(font, "View Cells", 8, 74, 0xD8E1ED, false);
        graphics.drawString(font, menu.connected() ? "ME-Netz verbunden" : "Kein aktives ME-Netz", 8, 104, menu.connected() ? 0x9DE0AE : 0xFFC080, false);
        graphics.drawString(font, playerInventoryTitle, 8, 115, 0xD8E1ED, false);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
    @Override public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key != 256 && (nameField.isFocused() || priorityField.isFocused())) {
            if (key == 257 || key == 335) { save(); return true; }
            if (key == 258) {
                EditBox next = nameField.isFocused() ? priorityField : nameField;
                nameField.setFocused(false); priorityField.setFocused(false);
                setFocused(next); next.setFocused(true); return true;
            }
            if (nameField.isFocused()) nameField.keyPressed(key, scanCode, modifiers);
            else priorityField.keyPressed(key, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }
}
