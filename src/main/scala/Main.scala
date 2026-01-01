package org.anaradedoros.scalacron
import domain.Models.*
import domain.Models.Minute.given
import dsl.CronDSL.{to, *}
import Days.*
import scala.language.postfixOps

object Main :
  def main(args: Array[String]): Unit =

    val job = CronJobExpr.build { c =>
      c.m = * / 5
      c.h = 12.h
      c.dom = 1.dom
      c.dow = 1.dow
    } >> "/usr/bin/backup.sh"
    println(job?)//same as job.schedule
    println(job) //whole expression as string

    val job2 =cron { c =>
      c.minute(* / 5)
      c.hour(9.h to 17.h)
      c.dom(1.dom)
      c.dow(Friday)
    } >> "/usr/bin/backup.sh"
    println(job2?) //same as job.schedule
    println(job2) //whole expression as string
