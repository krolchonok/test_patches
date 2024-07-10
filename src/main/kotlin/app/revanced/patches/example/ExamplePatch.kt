package app.revanced.patches.example

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch

@Patch(
    name = "Example Patch",
    description = "This is an example patch to start with.",
    compatiblePackages = [
        CompatiblePackage("org.telegram.messenger", ["10.14.4"]),
    ],
)
@Suppress("unused")
object ExamplePatch : BytecodePatch(emptySet()) {
    override fun execute(context: BytecodeContext) {
        // TODO("Not yet implemented")
    }
}
