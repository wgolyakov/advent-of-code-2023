import kotlin.math.max
import kotlin.math.min

typealias Part = Map<Char, Int>

class Rule(val workflow: String, val property: Char? = null, val less: Boolean? = null, val value: Int? = null) {
	fun isTrue(part: Part) = when (less) {
		true -> part[property]!! < value!!
		false -> part[property]!! > value!!
		else -> true
	}

	fun isCondition() = property != null
}

typealias Workflow = List<Rule>

fun main() {
	fun parse(input: List<String>): Pair<Map<String, Workflow>, List<Part>> {
		val separateIndex = input.indexOfFirst { it == "" }
		val workflows = input.subList(0, separateIndex).associate { wfStr ->
			val name = wfStr.substringBefore('{')
			val workflow = wfStr.substring(name.length + 1, wfStr.length - 1).split(',').map { rStr ->
				if (rStr.contains(':')) {
					val (condition, wf) = rStr.split(':')
					val property = condition[0]
					val less = condition[1] == '<'
					val value = condition.substring(2).toInt()
					Rule(wf, property, less, value)
				} else {
					Rule(rStr)
				}
			}
			name to workflow
		}
		val parts = input.subList(separateIndex + 1, input.size).map { pStr ->
			pStr.substring(1, pStr.length - 1).split(',').associate { prop ->
				prop.split("=").let{ it[0][0] to it[1].toInt() }
			}
		}
		return workflows to parts
	}

	fun part1(input: List<String>): Int {
		val (workflows, parts) = parse(input)
		var sum = 0
		for (part in parts) {
			var workflow = "in"
			while (workflow != "A" && workflow != "R") {
				for (rule in workflows[workflow]!!) {
					if (rule.isTrue(part)) {
						workflow = rule.workflow
						break
					}
				}
			}
			if (workflow == "A") sum += part.values.sum()
		}
		return sum
	}

	var count = 0L

	fun recurs(wfName: String, partRange: MutableMap<Char, IntRange>, workflows: Map<String, Workflow>) {
		if (wfName == "A") {
			count += partRange.values.map { it.last - it.first + 1L }.reduce { x, y -> x * y }
			return
		}
		if (wfName == "R") return
		val workflow = workflows[wfName]!!
		for (rule in workflow) {
			if (rule.isCondition()) {
				val r = partRange[rule.property!!]!!
				val rangeTrue = if (rule.less!!)
					r.first .. min(r.last, rule.value!! - 1)
				else
					max(r.first, rule.value!! + 1) .. r.last
				if (!rangeTrue.isEmpty()) {
					val newRanges = partRange.toMutableMap()
					newRanges[rule.property] = rangeTrue
					recurs(rule.workflow, newRanges, workflows)
				}
				val rangeFalse = if (rule.less)
					max(r.first, rule.value) .. r.last
				else
					r.first .. min(r.last, rule.value)
				if (rangeFalse.isEmpty()) break
				partRange[rule.property] = rangeFalse
			} else {
				recurs(rule.workflow, partRange.toMutableMap(), workflows)
			}
		}
	}

	fun part2(input: List<String>): Long {
		val (workflows, _) = parse(input)
		val partRange = mutableMapOf(
			'x' to 1..4000,
			'm' to 1..4000,
			'a' to 1..4000,
			's' to 1..4000
		)
		count = 0L
		recurs("in", partRange, workflows)
		return count
	}

	val testInput = readInput("Day19_test")
	check(part1(testInput) == 19114)
	check(part2(testInput) == 167409079868000L)

	val input = readInput("Day19")
	part1(input).println()
	part2(input).println()
}
