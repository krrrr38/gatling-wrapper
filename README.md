# gatling-wrapper

simulate any action with [gatling](http://gatling.io/)

## Usage

```sh
sbt "testOnly com.krrrr38.gatling_wrapper.simulation.ScalaSimulationExecutor" lastReport
```

## Customize

Please implement SimulationExecutor like following. See [examples](https://github.com/krrrr38/gatling-wrapper/tree/master/src/test/scala/com/krrrr38/gatling_wrapper/simulation).

```scala
class CustomSimulationExecutor extends SimulationExecutor {
  lazy val responseTime95 = 10
  lazy val action = new CustomAction(_)

  lazy val iss: Iterable[InjectionStep] = List(
    constantUsersPerSec(10) during (5 seconds)
  )
}

class CustomAction(val next: ActorRef) extends CustomSimulation[String] {
  override val buildAction: Session => String = {
    // build param for action
    // before every time action executed, this method is called.
    // and this method is not included execution time.
  }

  override val executeAction = (param: String) => {
    // implement simulated action
  }
}
```
