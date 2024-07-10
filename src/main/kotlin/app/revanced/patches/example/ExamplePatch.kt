package app.revanced.patches.example

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch

@Patch(
    name = "Example Patch",
    description = "This is an example patch to start with.",
    compatiblePackages = [
        CompatiblePackage("org.telegram.messenger"),
    ],
)
@Suppress("unused")
object ExamplePatch : BytecodePatch(emptySet()) {

    override fun execute(context: BytecodeContext) {
        // Find all api call to isChatNoForwards in MessagesController.
        buildMap {
            context.classes.forEach { classDef ->
                classDef.methods.let { methods ->
                    buildMap methodList@{
                        methods.forEach methods@{ method ->
                            with(method.implementation?.instructions ?: return@methods) {
                                ArrayDeque<Int>().also { patchIndices ->
                                    this.forEachIndexed { index, instruction ->
                                        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return@forEachIndexed

                                        val methodRef =
                                            (instruction as Instruction35c).reference as MethodReference
                                        if (methodRef.definingClass != "Lorg/telegram/messenger/MessagesController;" || methodRef.name != "isChatNoForwards") return@forEachIndexed

                                        patchIndices.add(index)
                                    }
                                }.also { if (it.isEmpty()) return@methods }.let { patches ->
                                    put(method, patches)
                                }
                            }
                        }
                    }
                }.also { if (it.isEmpty()) return@forEach }.let { methodPatches ->
                    put(classDef, methodPatches)
                }
            }
        }.forEach { (classDef, methods) ->
            with(context.proxy(classDef).mutableClass) {
                methods.forEach { (method, patches) ->
                    with(findMutableMethodOf(method)) {
                        while (!patches.isEmpty()) {
                            val index = patches.removeLast()
                            replaceWithFalse(index)
                        }
                    }
                }
            }
        }
    }

    // Replace the method call with returning false
    private fun MutableMethod.replaceWithFalse(index: Int) {
        addInstructions(
            index,
            """
                const/4 v0, 0x0
                move-result v0
            """,
        )
    }
}
