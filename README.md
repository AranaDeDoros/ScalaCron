## ScalaCron

An attempt at making a cronjob domain second language. **In order to increase flexibility expressions are built using **unsafe** , 
instead of **_applyValidated_** which would be the proper method.** 

**Usage**
```scala
import domain.Models.*
import domain.Models.Minute.given
import dsl.CronDSL.*
import java.time.DayOfWeek
import scala.language.postfixOps

val job2 = cron { c =>
  c.minute(* / 5)
  c.hour(12.h)
  c.dom(1.dom)
  c.dow(DayOfWeek.FRIDAY)
} >> "/usr/bin/backup.sh"

println(job2?) //same as job.schedule
println(job2) //whole expression as string

//still redefining this thing

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
 
