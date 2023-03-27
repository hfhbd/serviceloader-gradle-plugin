import app.softwork.serviceloader.ServiceLoader;
import sample.Provider;

@ServiceLoader(forClass = Provider.class)
class JavaFoo implements Provider {
    
}
