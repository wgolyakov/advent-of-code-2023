enum class ModuleType(val t: String) {
	FlipFlop("%"),
	Conjunction("&"),
	Broadcast("")
}

fun main() {
	class Pulse(val high: Boolean, val from: String, val to: String) {
		override fun toString() = "$from -${if (high) "high" else "low"}-> $to"
	}

	class Module(val name: String, var type: ModuleType? = null) {
		val outs = mutableListOf<String>()
		val inputs = mutableMapOf<String, Boolean>()
		var lowPulseCount = 0
		var highPulseCount = 0
		var state = false

		fun pulse(pulse: Pulse, queue: MutableList<Pulse>) {
			if (pulse.high) highPulseCount++ else lowPulseCount++
			when (type) {
				ModuleType.FlipFlop -> if (!pulse.high) {
					state = !state
					for (out in outs) queue.add(Pulse(state, name, out))
				}
				ModuleType.Conjunction -> {
					inputs[pulse.from] = pulse.high
					val low = inputs.values.all { it }
					for (out in outs) queue.add(Pulse(!low, name, out))
				}
				ModuleType.Broadcast -> {
					for (out in outs) queue.add(Pulse(pulse.high, name, out))
				}
				else -> {}
			}
		}

		override fun toString() = "${type?.t}$name, low: $lowPulseCount, high: $highPulseCount"
	}

	fun parse(input: List<String>): Map<String, Module> {
		val modules = mutableMapOf<String, Module>()
		for (line in input) {
			val (s1, s2) = line.split(" -> ")
			val type = when (s1[0]) {
				'%' -> ModuleType.FlipFlop
				'&' -> ModuleType.Conjunction
				else -> ModuleType.Broadcast
			}
			val name = if (type == ModuleType.Broadcast) s1 else s1.drop(1)
			modules[name]?.type = type
			val module = modules.getOrPut(name) { Module(name, type) }
			for (out in s2.split(", ")) {
				module.outs.add(out)
				modules.getOrPut(out) { Module(out) }.inputs[name] = false
			}
		}
		return modules
	}

	fun part1(input: List<String>): Int {
		val modules = parse(input)
		for (i in 0 until 1000) {
			val startPulse = Pulse(false, "button", "broadcaster")
			val queue = mutableListOf(startPulse)
			while (queue.isNotEmpty()) {
				val pulse = queue.removeFirst()
				modules[pulse.to]?.pulse(pulse, queue)
			}
		}
		return modules.values.sumOf { it.lowPulseCount } * modules.values.sumOf { it.highPulseCount }
	}

	fun part2(input: List<String>): Long {
		val modules = parse(input)
		val rx = modules["rx"] ?: error("Module rx not found")
		if (rx.inputs.size != 1) error("Solution works only when rx has 1 input")
		val md = modules[rx.inputs.keys.first()]!!
		if (md.type != ModuleType.Conjunction) error("Solution works only when rx has 1 input from conjunction module")
		val highCounts = mutableListOf<Long>()
		var buttonPressCount = 0
		while (highCounts.size < md.inputs.size) {
			buttonPressCount++
			val startPulse = Pulse(false, "button", "broadcaster")
			val queue = mutableListOf(startPulse)
			while (queue.isNotEmpty()) {
				val pulse = queue.removeFirst()
				modules[pulse.to]?.pulse(pulse, queue)
				if (pulse.to == md.name && pulse.high) highCounts.add(buttonPressCount.toLong())
			}
		}
		return highCounts.reduce { x, y -> x * y }
	}

	check(part1(readInput("Day20_test")) == 32000000)
	check(part1(readInput("Day20_test2")) == 11687500)

	val input = readInput("Day20")
	part1(input).println()
	part2(input).println()
}
