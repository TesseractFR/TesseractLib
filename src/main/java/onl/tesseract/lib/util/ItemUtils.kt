package onl.tesseract.lib.util

import org.bukkit.inventory.ItemStack


fun ItemStack.isSameItem(anotherItem: ItemStack): Boolean {
    return areSameItem(this, anotherItem);
}

fun areSameItem(item: ItemStack?,anotherItem: ItemStack?): Boolean {
    if(anotherItem == null || item == null) return false
    if(item.type != anotherItem.type) return false
    if(item.enchantments != anotherItem.enchantments) return false
    if(item.itemMeta != anotherItem.itemMeta) return false
    return true
}