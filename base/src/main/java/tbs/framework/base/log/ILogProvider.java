package tbs.framework.base.log;

public interface ILogProvider {
    ILogger getLogger(String name);
}
