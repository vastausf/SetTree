import exception.DuplicatedNode
import kotlin.Throws

/**
 * The basic item of SetTree.
 */
class SetTree<T>(
    val value: T
) {
    var parent: SetTree<T>? = null
    val child: MutableList<SetTree<T>> = mutableListOf()

    override fun toString(): String {
        return "$value -> $child"
    }

    /**
     * Returns the root of this tree.
     * @return A node that does not have a parent.
     */
    fun findRoot(): SetTree<T> {
        var cursor = this

        while (cursor.parent != null) {
            cursor = cursor.parent!!
        }

        return cursor
    }

    /**
     * Creates the child node and check duplicates in this branch.
     * @param value Value of the node to add.
     * @throws DuplicatedNode If tree already contains node with equivalent value.
     */
    @Throws(DuplicatedNode::class)
    fun addChild(value: T): SetTree<T> {
        val newNode = SetTree(value)

        checkDuplicates(newNode)?.let { node ->
            throw DuplicatedNode(node)
        }

        newNode.parent = this

        child.add(newNode)

        return newNode
    }

    /**
     * Removes node from parent and clear parent-child link.
     * Do not call this while child nodes iterates.
     */
    fun remove(node: SetTree<T>) {
        node.parent = null

        child.remove(node)
    }

    /**
     * Move up the tree and compares nodes by .equals()
     * Return null if no collision found.
     * Else, it returns the equivalent node.
     */
    fun checkDuplicates(
        node: SetTree<T>
    ): SetTree<T>? {
        var cursor = this

        while (cursor.parent != null) {
            cursor = cursor.parent!!

            if (cursor.value == node.value) return cursor
        }

        return null
    }

    /**
     * Iterate tree branches.
     *
     *           1
     *          / \
     *         /   \
     *        2     3
     *       /     / \
     *      /     /   \
     *     4     5     7
     *
     *  For example. This tree will call pairs in order:
     *  2 -> 4
     *  1 -> 2
     *  3 -> 5
     *  3 -> 7
     *  1 -> 3
     */
    fun iteratePairs(
        block: (parent: T, child: T) -> Unit
    ) {
        child.forEach { setTree ->
            setTree.iteratePairs(block)
        }

        val hasParentValue = parent?.value

        if (hasParentValue != null) {
            block(hasParentValue, value)
        }
    }

    /**
     * Iterates through a tree from its root.
     *
     *           1
     *          / \
     *         /   \
     *        2     3
     *       /     / \
     *      /     /   \
     *     4     5     7
     *
     * Will call in order: 1, 2, 3, 4, 5, 7.
     */
    fun iterateOut(
        block: (node: SetTree<T>) -> Unit
    ) {
        block(this)

        child.forEach { setTree ->
            setTree.iterateOut(block)
        }
    }

    /**
     * Iterates through a tree from its root.
     *
     *           1
     *          / \
     *         /   \
     *        2     3
     *       /     / \
     *      /     /   \
     *     4     5     7
     *
     * Will call in order: 4, 2, 5, 7, 3, 1.
     */
    fun iterateIn(
        block: (node: SetTree<T>) -> Unit
    ) {
        child.forEach { setTree ->
            setTree.iterateIn(block)
        }

        block(this)
    }

    /**
     * Removes all branches and recreate the tree.
     * @param createPredication Adds all returned nodes to the parent node.
     */
    fun invalidate(
        createPredication: (parent: SetTree<T>) -> List<T>
    ) {
        iterateOut { node ->
            node.parent = null
            node.child.clear()
        }

        iterateOut { node ->
            createPredication(node).forEach { child ->
                node.addChild(child)
            }
        }
    }
}
