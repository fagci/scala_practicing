import scala.math.pow

@main def main() =
  (123 to 987).map { n =>
    var kub = n.toString.map(_.asDigit)
    if (pow(kub.sum, 3) == n) {
      println(s"(${kub.mkString("+")})^3=$n")
    }
  }
