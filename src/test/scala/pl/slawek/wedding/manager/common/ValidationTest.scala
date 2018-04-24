package pl.slawek.wedding.manager.common

import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks._

class ValidationTest extends FunSuite {

  val properEmails =
    Table("email", "test@gmail.com", "test@91.com")

  forAll(properEmails) { email =>
    assert(Validation.isEmailValid(email))
  }

  val improperEmails =
    Table("email", "test@gmailcom", "testgma.i.l.c.o.m", "test@gmailcom")

  forAll(improperEmails) { email =>
    assert(!Validation.isEmailValid(email))
  }
}
