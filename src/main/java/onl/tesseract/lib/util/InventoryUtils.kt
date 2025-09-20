package onl.tesseract.lib.util

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * Compte le nombre d'item identique dans un inventaire
 */
fun Inventory.countItem(target: ItemStack): Int {
    return this.storageContents
            .filterNotNull()
            .filter { it.isSameItem(target) }
            .sumOf { it.amount }
}

fun Inventory.availableSpace(target: ItemStack): Int{
    val notFullSlot =  this.storageContents
            .filterNotNull()
            .filter { it.isSameItem(target) }
            .sumOf { target.maxStackSize - it.amount }
    val emptySlot = this.storageContents.count { it == null } * target.maxStackSize
    return notFullSlot+emptySlot
}

/**
 * Retire un certain nombre d'un item spécifique dans un inventaire.
 * @param target L'item à retirer.
 * @param amount La quantité à retirer.
 */
fun Inventory.removeItem(target: ItemStack, amount: Int) {
    var remaining = amount

    this.storageContents.forEachIndexed { index, item ->
        if (item != null && item.isSameItem(target)) {
            val toRemove = minOf(remaining, item.amount)
            item.amount -= toRemove
            remaining -= toRemove

            if (item.amount <= 0) {
                this.storageContents[index] = null
            }

            if (remaining <= 0) return
        }
    }
}

/**
 * Ajoute un certain nombre d'un item spécifique dans un inventaire.
 * @param target L'item à ajouter.
 * @param amount La quantité à ajouter.
 */
fun Inventory.addItem(target: ItemStack, amount: Int) {
    val toAdd = target.clone()
    toAdd.amount = amount
    this.addItem(toAdd)
}

