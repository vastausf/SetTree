data class Variable(
    val key: String,
    var value: String
)

fun main() {
    val variableTree = treeRootOf(
        Variable(
            "root",
            "Hello, @{name}! @{message}."
        )
    )

    invalidate(variableTree)
    println(variableTree)
    println(build(variableTree))

    println("================================")

    variableTree.value.value = "Hello, @{name}! @{message}. From @{part1}."

    invalidate(variableTree)
    println(variableTree)
    println(build(variableTree))

    println("================================")

    variableTree.value.value = "@{message}"

    invalidate(variableTree)
    println(variableTree)
    println(build(variableTree))
}

fun invalidate(
    setTree: SetTree<Variable>
) {
    val regex = """(?<=@\{)([A-Za-z0-9])+(?=})""".toRegex()

    val variableList = listOf(
        Variable(
            "name",
            "Scarlet"
        ),
        Variable(
            "message",
            "Message with @{part1} and @{part2}"
        ),
        Variable(
            "part1",
            "**part1**"
        ),
        Variable(
            "part2",
            "&&part2&&"
        )
    )

    setTree.invalidate { parent ->
        val usedVariables = regex.findAll(parent.value.value)

        usedVariables
            .map { usedVariable ->
                variableList.firstOrNull { variable ->
                    usedVariable.value == variable.key
                }
            }
            .filterNotNull()
            .toList()
    }

    val n1 = treeRootOf(1)

    val n2 = n1.addChild(2)
    val n3 = n1.addChild(3)

    val n4 = n2.addChild(4)
    val n5 = n3.addChild(5)
    val n7 = n3.addChild(7)

    n1.iterateIn {
        println(it.value)
    }
}


fun build(
    setTree: SetTree<Variable>
): String {
    setTree.iteratePairs { parent, child ->
        parent.value = parent.value.replace("@{${child.key}}", child.value)
    }

    return setTree.value.value
}
