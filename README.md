## ScalaCron

An attempt at making a cronjob domain second language. In order to increase the flexibility of this thing, I had to 
temporarily use **unsafe** to build the expressions present derived from [CronField](src/main/scala/domain/Models.scala), 
instead of **applyValidated** which is the proper,
validating option. 

**Usage**
```scala
import dsl.DSL.CronDSL.*

val job = CronJobExpr.build { c =>
  c.m = * / 5
  c.h = 12.h
  c.dom = 1.dom
  c.dow = 1.dow
} >> ("/usr/bin/backup.sh")

println(job?)//same as job.schedule
println(job) //whole expression as string
```
```text
- Minute        : every 5 minutes
- Hour          : at 12
- Day of Month  : at 1
- Day of Week   : Monday
*/5 12 1 1 /usr/bin/backup.sh
```

## TODO ##
1. [ ] Re-enable application of applyValidated
2. [ ] Pretty token explanation.
 
