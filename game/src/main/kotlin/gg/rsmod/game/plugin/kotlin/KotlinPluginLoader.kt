package gg.rsmod.game.plugin.kotlin

import com.google.inject.Injector
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.event.EventBus
import io.github.classgraph.ClassGraph

class KotlinPluginLoader(
    private val injector: Injector,
    private val eventBus: EventBus,
    private val actions: ActionBus
) {

    fun load(): List<KotlinPlugin> {
        val plugins = mutableListOf<KotlinPlugin>()
        ClassGraph().enableAllInfo().scan().use { result ->
            val subclasses = result.getSubclasses(KotlinPlugin::class.java.name).directOnly()
            subclasses.forEach { subclass ->
                val loadedClass = subclass.loadClass(KotlinPlugin::class.java)
                val constructor = loadedClass.getConstructor(
                    Injector::class.java,
                    EventBus::class.java,
                    ActionBus::class.java
                )
                val instance = constructor.newInstance(
                    injector,
                    eventBus,
                    actions
                )
                plugins.add(instance)
            }
        }
        return plugins
    }
}
