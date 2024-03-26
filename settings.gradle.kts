
rootProject.name = "hexagon"

include(
    "core",
    "handlers",
    "handlers_mutable",
    "site",
    "starters",
)

includeNestedModules(
    "http",
    "serialization",
//    "serverless",
    "templates"
)

fun includeNestedModules(vararg directories: String) {
    directories.forEach(::includeNestedModules)
}

fun includeNestedModules(directory: String) {
    val dir = rootDir.resolve(directory)

    if (!dir.exists() || !dir.isDirectory)
        error("$directory directory must exist")

    include(":$directory")

    dir.listFiles()
        ?.filter { it.isDirectory && it.resolve("build.gradle.kts").isFile }
        ?.forEach {
            val name = it.name
            include(":$directory:$name")
            project(":$directory:$name").projectDir = it
        }
}
