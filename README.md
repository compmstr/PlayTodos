
# PlayTodos

A test of the play framework, as a Todos app

## Notes

### Running in the console

    play -DapplyEvolutions.default=true
	...
	[PlayTodos] $ console
	...
    scala> new play.core.StaticApplication(new java.io.File("."))

    Or you can now run it with (without needing to do the -DapplyEvol...:
	scala> controllers.Application.consoleApp

And then you can access your objects normally
