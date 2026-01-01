## ScalaCron

My first embedded DSL experiment. ScalaCron can generate Cronjob expressions programmatically.

**Usage**
```scala
import domain.Models.*
import domain.Models.Minute.given
import dsl.CronDSL.{to, *}
import Days.*
import scala.language.postfixOps


import scala.language.postfixOps
object Main :
  def main(args: Array[String]): Unit =

  val job2 = cron { c =>
    c.minute(* / 5)
    c.hour(9.h to 17.h)
    c.dom(1.dom)
    c.dow(Friday)
  } >> "/usr/bin/backup.sh"
  println(job2?) //same as job.schedule
  println(job2) //whole expression as string


```
```text
- Minute        : every 5 minutes
- Hour          : at 12
- Day of Month  : at 1
- Day of Week   : Monday
*/5 12 1 1 /usr/bin/backup.sh
```


 
