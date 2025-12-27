package org.anaradedoros.scalacron
import domain.Models.*
import domain.Models.Minute.given
import dsl.{>>, ?, run}
import scala.language.postfixOps
object Main {
  def main(args: Array[String]): Unit = {

    import dsl.CronDSL.*

    val job = CronJobExpr.build { c =>
      c.m = * / 5
      c.h = 12.h
      c.dom = 1.dom
      c.dow = 1.dow
    } >> ("/usr/bin/backup.sh")

    println(job?)//same as job.schedule
    println(job) //whole expression as string

  }
}
