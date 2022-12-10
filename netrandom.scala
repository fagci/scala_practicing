import scala.util.{Random,Try}
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.{Socket, InetSocketAddress}

val r = new Random()

def intIp() = r.between(0x01000000L, 0xe0000000L)

def int2Ip(i:Long) = s"${i>>24&255}.${i>>16&255}.${i>>8&255}.${i&255}"

val excludeRanges:List[(Long, Long)] = List(
  (0x0A000000, 0x0b000000), // 10.0.0.0 - 10.255.255.255
  (0x7F000000, 0x80000000), // 127.0.0.0 - 127.255.255.255
  (0x64400000, 0x64800000), // 100.64.0.0 - 100.127.255.255
  (0xAC100000, 0xac200000), // 172.16.0.0 - 172.31.255.255
  (0xC6120000, 0xc6140000), // 198.18.0.0 - 198.19.255.255
  (0xA9FE0000, 0xa9ff0000), // 169.254.0.0 - 169.254.255.255
  (0xC0A80000, 0xc0a90000), // 192.168.0.0 - 192.168.255.255
  (0xC0000000, 0xc0000100), // 192.0.0.0 - 192.0.0.255
  (0xC0000200, 0xc0000300), // 192.0.2.0 - 192.0.2.255
  (0xc0586300, 0xc0586400), // 192.88.99.0 - 192.88.99.255
  (0xC6336400, 0xc6336500), // 198.51.100.0 - 198.51.100.255
  (0xCB007100, 0xcb007200), // 203.0.113.0 - 203.0.113.255
  (0xe9fc0000, 0xe9fc0100) // 233.252.0.0 - 233.252.0.255
)

def notGlobal(intip:Long) =
  excludeRanges.exists((f,t) => f <= intip && intip < t)

def randIp():String =
  val intip = intIp()
  if(notGlobal(intip)) randIp() else int2Ip(intip)

def check(ip:String):Boolean =
  val addr = new InetSocketAddress(ip, 80)
  val s = new Socket()
  Try{
    s.connect(addr, 550)
    s.close()
  }.isSuccess

def work = Future {
  Iterator
    .continually(randIp())
    .filter(check)
    .foreach(println)
}

@main def main() =
  val tasks = List.fill(1024)(work)
  Await.result(Future.sequence(tasks), Duration.Inf)
  
