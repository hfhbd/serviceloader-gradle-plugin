import app.softwork.serviceloader.*

interface Provider

@ServiceLoader(Provider::class)
class Impl : Provider
