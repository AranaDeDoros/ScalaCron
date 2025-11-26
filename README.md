## Cronjob DSL test

An attempt at making a cronjob domain second language. In order to increase the flexibility of this thing, I had to 
temporarily use **unsafe** to build the expressions present derived from [CronField](src/main/scala/domain/Models.scala), 
instead of **applyValidated** which is the proper,
validating option. 

**Usage**
```scala
  import CronDSL.*

    val job = CronJobExpr.build { c =>
      c.m = * / 5
      c.h = 12.h
      c.dom = 1.dom
      c.dow = 1.dow
    }

    println(job) //*/5 12 1 1

```

## TODO ##
1. [ ] Renable application of applyValidated
 
