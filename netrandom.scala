import scala.util.Random
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.{Socket, InetSocketAddress}

var r = new Random()

def inRange( n: Long, r: (Long, Long) ) = (r._1 <= n && n < r._2)

val ranges:List[(Long, Long)] = List(
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

def notGlobal(intip:Long):Boolean =
  ranges.exists(inRange(intip, _))

def intIp():Long =
  0x01000000 + r.nextLong(0xdeffffff)

def int2Ip(i:Long):String =
  s"${i>>24&255}.${i>>16&255}.${i>>8&255}.${i&255}"

def rand_ip():String =
  var i =  intIp()
  if(notGlobal(i)) return rand_ip()
  int2Ip(i)

def check(ip:String):Boolean =
  try {
    val s = new Socket()
    s.connect(new InetSocketAddress(ip, 80), 750)
    s.close()
    true
  } catch {
    case _ => false
  }

def work(i:Long) = Future {
  while(true) {
    val ip = rand_ip()
    if(check(ip))
      println(s"$i: ${ip}")
  }
}

@main def main() =
  var tasks = (1L to 1024).map(work)
  Await.result(Future.sequence(tasks), Duration.Inf)
  