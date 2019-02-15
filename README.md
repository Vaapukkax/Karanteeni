# Karanteeni
This README file is made to help understand how KaranteeniCore should be used

## Code examples of KaranteeniCore
### How to extend KaranteeniCore
```java
public class MyPlugin extends KaranteeniPlugin
{
  public MyPlugin()
  {
    //Does this plugin use the translator service of KaranteeniCore
    super(true);
  }
}
```

### Creating commands and using translation engine
MyCommand.java
```java
public class MyCommand extends AbstractCommand implements TranslationContainer
{
  public MyCommand()
  {
    super(MyPlugin.getPlugin(MyPlugin.class), "command", "usage", "description", Arrays.asList("possible","command","arguments"));
    registerTranslations();
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
  {
    //Returns a translation based on the users language if player
    sender.sendMessage(MyPlugin.getTranslator().getTranslation(MyPlugin.getPlugin(MyPlugin.class),
      sender, "sample-text"));
    
    if(args.length == 1)
      if(this.getRealParam(args[0]).equalsIgnoreCase("arguments"))
        sender.sendMessage(MyPlugin.getTranslator().getTranslation(MyPlugin.getPlugin(MyPlugin.class),
          sender, "cmd-args"));
  }
  
  @Override
  public void registerTranslations() 
  {
    MyPlugin.getTranslator().registerTranslation(
    MyPlugin.getPlugin(MyPlugin.class), "sample-text", "This is sample text");
    MyPlugin.getPlugin(MyPlugin.class), "cmd-args", "Translated command arguments!");
  }
}
```

### Getting default messages (strings)
```java
MyPlugin.getDefaultMsgs().noPermission(CommandSender);
MyPlugin.getDefaultMsgs().playerNotFound(CommandSender);
MyPlugin.getDefaultMsgs().defaultNotForConsole();
```

### Accessing database
```java
MyPlugin.getDatabaseConnector();
```

