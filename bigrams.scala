@main def main(args: String*) =   
  var bg = args
    .mkString(" ")
    .split("\\W+")
    .flatMap(_.sliding(2))
    .groupBy(identity).map(_._1)

  println(bg.mkString(" "))
