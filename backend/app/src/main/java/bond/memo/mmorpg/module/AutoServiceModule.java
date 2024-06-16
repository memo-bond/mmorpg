package bond.memo.mmorpg.module;

import bond.memo.mmorpg.repository.Repository;
import bond.memo.mmorpg.service.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.reflections.Reflections;

import java.util.Set;

public class AutoServiceModule extends AbstractModule {

    private static final String SERVICE_PACKAGE = "bond.memo.mmorpg.service";
    private static final String REPOSITORY_PACKAGE = "bond.memo.mmorpg.repository";
    private static final AutoServiceModule INSTANCE = new AutoServiceModule();

    public static AutoServiceModule of() {
        return INSTANCE;
    }

    @Override
    protected void configure() {
        autoBind(SERVICE_PACKAGE, Service.class);
        autoBind(REPOSITORY_PACKAGE, Repository.class);
    }

    private <T> void autoBind(String packageName, Class<T> type) {
        Multibinder<T> typeBinder = Multibinder.newSetBinder(binder(), type);
        Reflections typeReflections = new Reflections(packageName);
        Set<Class<? extends T>> classes = typeReflections.getSubTypesOf(type);
        for (Class<? extends T> impl : classes) {
            if (impl.getName().endsWith("Impl"))
                typeBinder.addBinding().to(impl);
        }
    }
}
