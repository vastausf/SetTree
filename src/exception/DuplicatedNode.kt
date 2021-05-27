package exception

import SetTree

class DuplicatedNode(
    node: SetTree<*>
) : Exception("Node with value '${node.value.toString()}' already exists in this branch")
