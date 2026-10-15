package local.noto.housekeeper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HousekeeperApplication

fun main(args: Array<String>) {
    runApplication<HousekeeperApplication>(*args)
}
