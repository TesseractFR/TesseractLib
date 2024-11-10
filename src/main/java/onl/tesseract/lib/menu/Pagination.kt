package onl.tesseract.lib.menu

class Pagination<T>(
    val menu: Menu,
    val area: Area,
    val elements: List<T>,
    val buttonSupplier: (T) -> AButton,
    val previousIndex: Int,
    val nextIndex: Int,
) {
    var page: Int = 0

    data class Area(val start: Int, val width: Int, val height: Int)

    fun displayPage(page: Int) {
        this.page = page
        menu.clear()

        val maxPage = elements.size / getCountPerPage()
        val start = getCountPerPage() * page
        val end = getCountPerPage() * (page + 1) - 1
        val indexIterator = getAreaIndices().iterator()
        for (i in start..end) {
            menu.addButton(indexIterator.next(), buttonSupplier(elements[i]))
        }

        menu.addButton(
            previousIndex,
            ItemBuilder(Menu.getBackButton())
                .name("Précedent")
                .build()
        ) {
            displayPage((page - 1).coerceAtLeast(0))
        }
        menu.addButton(
            nextIndex,
            ItemBuilder(Menu.getBackButton())
            .name("Suivant")
            .build()
        ) {
            displayPage((page + 1).coerceAtMost(maxPage - 1))
        }
    }

    private fun getAreaIndices(): List<Int> {
        val startCol = area.start % 9
        val startRow = area.start / 9
        val indices: MutableList<Int> = mutableListOf()
        for (i in startRow..(startRow + area.height)) {
            for (j in startCol..(startCol + area.width)) {
                indices.add(i * 9 + j)
            }
        }
        return indices
    }

    private fun getCountPerPage(): Int {
        return area.width * area.height
    }
}
