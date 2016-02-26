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

class CusotomAction(val next: ActorRef) extends CustomSimulation {
  override val executeAction = (session: Session) => {
    // implement simulated action
  }
}
```
