import java.math.BigInteger

fun main() {
	val b0 = BigInteger.ZERO
	val b1 = BigInteger.ONE

	data class Point(val x: BigInteger, val y: BigInteger, val z: BigInteger)

	data class Axis(val p: BigInteger, val v: BigInteger) {
		constructor(p: Long, v: Long): this(p.toBigInteger(), v.toBigInteger())
	}

	data class Hailstone(val p: Point, val v: Point) {
		val x = Axis(p.x, v.x)
		val y = Axis(p.y, v.y)
		val z = Axis(p.z, v.z)
		fun x(t: BigInteger) = p.x + t * v.x
		fun y(t: BigInteger) = p.y + t * v.y
		fun z(t: BigInteger) = p.z + t * v.z
		fun x(t: Int) = x(t.toBigInteger())
		fun y(t: Int) = y(t.toBigInteger())
		fun z(t: Int) = z(t.toBigInteger())
	}

	fun parse(input: List<String>): List<Hailstone> {
		return input.map { line ->
			val (s1, s2) = line.split(" @ ")
			val (x, y, z) = s1.split(", ").map { it.toBigInteger() }
			val (vx, vy, vz) = s2.split(", ").map { it.trim().toBigInteger() }
			Hailstone(Point(x, y, z), Point(vx, vy, vz))
		}
	}

	fun inRay(p: BigInteger, hp: BigInteger, hv: BigInteger): Boolean {
		return (hv > b0 && p >= hp) || (hv < b0 && p <= hp) || (hv == b0 && p == hp)
	}

	// Intersection point of two 2D lines, with rounded coordinates
	fun cross2DLines(h1: Hailstone, h2: Hailstone): Point? {
		val x1 = h1.p.x
		val y1 = h1.p.y
		val x2 = x1 + h1.v.x
		val y2 = y1 + h1.v.y
		val x3 = h2.p.x
		val y3 = h2.p.y
		val x4 = x3 + h2.v.x
		val y4 = y3 + h2.v.y
		val d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
		if (d == b0) return null
		val a = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)
		val b = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)
		val x = a / d
		val y = b / d
		if (!inRay(x, h1.p.x, h1.v.x)) return null
		if (!inRay(y, h1.p.y, h1.v.y)) return null
		if (!inRay(x, h2.p.x, h2.v.x)) return null
		if (!inRay(y, h2.p.y, h2.v.y)) return null
		return Point(x, y, b0)
	}

	fun part1(input: List<String>, area: LongRange = 200000000000000..400000000000000): Int {
		val hailstones = parse(input)
		var count = 0
		for ((i, h1) in hailstones.withIndex()) {
			for (h2 in hailstones.subList(i + 1, hailstones.size)) {
				val p = cross2DLines(h1, h2)
				if (p != null && p.x.toLong() in area && p.y.toLong() in area) count++
			}
		}
		return count
	}

	fun goodT(s: Axis, h: Axis): Boolean {
		if (s.p == h.p && s.v == h.v) return true
		if (h.v == s.v) return false
		if ((s.p - h.p) % (h.v - s.v) != b0) return false
		val t = (s.p - h.p) / (h.v - s.v)
		return t > b0
	}

	fun findStoneAxis(hailstones: List<Axis>): Axis? {
		val maxP = hailstones.maxOf { it.p.abs() }.toLong()
		val maxV = hailstones.maxOf { it.v.abs() }.toLong()
		for (p0 in 0 .. maxP * 2) {
			for (v in 1 .. maxV * 2) {
				if (hailstones.all { goodT(Axis(p0, v), it) }) return Axis(p0, v)
				if (hailstones.all { goodT(Axis(p0, -v), it) }) return Axis(p0, -v)
				if (hailstones.all { goodT(Axis(-p0, v), it) }) return Axis(-p0, v)
				if (hailstones.all { goodT(Axis(-p0, -v), it) }) return Axis(-p0, -v)
			}
		}
		return null
	}

	fun bruteForce(hailstones: List<Hailstone>): Long {
		val x = findStoneAxis(hailstones.map { it.x }) ?: error("X not found")
		val y = findStoneAxis(hailstones.map { it.y }) ?: error("Y not found")
		val z = findStoneAxis(hailstones.map { it.z }) ?: error("Z not found")
		return x.p.toLong() + y.p.toLong() + z.p.toLong()
	}

	// Calculate stone trajectory by two parallel lines in 3D space
	fun calcByParallelLines(h1: Hailstone, h2: Hailstone, h3: Hailstone, hailstones: List<Hailstone>): Hailstone? {
		// 3 points of 2 parallel lines
		val x1 = h1.p.x
		val y1 = h1.p.y
		val z1 = h1.p.z
		val x2 = h2.p.x
		val y2 = h2.p.y
		val z2 = h2.p.z
		val x3 = h3.p.x
		val y3 = h3.p.y
		val z3 = h3.p.z

		// Plain by 3 points: Ax + By + Cz + D = 0
		// Trajectory of the stone lies in this plane
		val a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2)
		val b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2)
		val c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)
		val d = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1))

		// Looking for any two lines intersecting the plane
		for ((h4, h5) in (hailstones - h1 - h2).windowed(2)) {
			// Times at which lines intersect the plane: t = -(Ax + By + Cz) / (AVx + BVy + CVz)
			if (a * h4.v.x + b * h4.v.y + c * h4.v.z == b0) continue
			if (a * h5.v.x + b * h5.v.y + c * h5.v.z == b0) continue
			if ((a * h4.p.x + b * h4.p.y + c * h4.p.z + d) % (a * h4.v.x + b * h4.v.y + c * h4.v.z) != b0) continue
			if ((a * h5.p.x + b * h5.p.y + c * h5.p.z + d) % (a * h5.v.x + b * h5.v.y + c * h5.v.z) != b0) continue
			val t4 = -(a * h4.p.x + b * h4.p.y + c * h4.p.z + d) / (a * h4.v.x + b * h4.v.y + c * h4.v.z)
			val t5 = -(a * h5.p.x + b * h5.p.y + c * h5.p.z + d) / (a * h5.v.x + b * h5.v.y + c * h5.v.z)

			// Points where lines intersect the plane
			val x4 = h4.x(t4)
			val y4 = h4.y(t4)
			val z4 = h4.z(t4)

			val x5 = h5.x(t5)
			val y5 = h5.y(t5)
			val z5 = h5.z(t5)

			// Trajectory of the stone passes through these points
			if (t5 == t4) continue
			if ((x5 - x4) % (t5 - t4) != b0) continue
			if ((y5 - y4) % (t5 - t4) != b0) continue
			if ((z5 - z4) % (t5 - t4) != b0) continue

			// Stone velocity
			val vx = (x5 - x4) / (t5 - t4)
			val vy = (y5 - y4) / (t5 - t4)
			val vz = (z5 - z4) / (t5 - t4)

			// Stone initial position
			val x0 = x4 - vx * t4
			val y0 = y4 - vy * t4
			val z0 = z4 - vz * t4

			return Hailstone(Point(x0, y0, z0), Point(vx, vy, vz))
		}
		return null
	}

	fun toXYT(h: Hailstone, t: Int) = Hailstone(Point(h.x(t), h.y(t), t.toBigInteger()), Point(h.v.x, h.v.y, b1))
	fun toXTZ(h: Hailstone, t: Int) = Hailstone(Point(h.x(t), t.toBigInteger(), h.z(t)), Point(h.v.x, b1, h.v.z))
	fun toTYZ(h: Hailstone, t: Int) = Hailstone(Point(t.toBigInteger(), h.y(t), h.z(t)), Point(b1, h.v.y, h.v.z))

	// Calculate stone trajectory by two parallel lines in XYT space
	fun calcByParallelXYT(h1: Hailstone, h2: Hailstone, hailstones: List<Hailstone>): Hailstone? {
		// 3 points of 2 parallel lines in XYT space
		val p1 = toXYT(h1, 0)
		val p2 = toXYT(h2, 0)
		val p3 = toXYT(h1, 1000) // any
		// Hailstones in XYT space
		val hs = hailstones.map { toXYT(it, 0) }
		return calcByParallelLines(p1, p2, p3, hs)
	}

	// Calculate stone trajectory by two parallel lines in XTZ space
	fun calcByParallelXTZ(h1: Hailstone, h2: Hailstone, hailstones: List<Hailstone>): Hailstone? {
		// 3 points of 2 parallel lines in XTZ space
		val p1 = toXTZ(h1, 0)
		val p2 = toXTZ(h2, 0)
		val p3 = toXTZ(h1, 1000) // any
		// Hailstones in XTZ space
		val hs = hailstones.map { toXTZ(it, 0) }
		return calcByParallelLines(p1, p2, p3, hs)
	}

	// Calculate stone trajectory by two parallel lines in TYZ space
	fun calcByParallelTYZ(h1: Hailstone, h2: Hailstone, hailstones: List<Hailstone>): Hailstone? {
		// 3 points of 2 parallel lines in TYZ space
		val p1 = toTYZ(h1, 0)
		val p2 = toTYZ(h2, 0)
		val p3 = toTYZ(h1, 1000) // any
		// Hailstones in TYZ space
		val hs = hailstones.map { toTYZ(it, 0) }
		return calcByParallelLines(p1, p2, p3, hs)
	}

	fun isParallelXY(h1: Hailstone, h2: Hailstone) = h1.v.x == h2.v.x && h1.v.y == h2.v.y
	fun isParallelXZ(h1: Hailstone, h2: Hailstone) = h1.v.x == h2.v.x && h1.v.z == h2.v.z
	fun isParallelYZ(h1: Hailstone, h2: Hailstone) = h1.v.y == h2.v.y && h1.v.z == h2.v.z

	fun part2(input: List<String>): Long {
		val hailstones = parse(input)
		var x0: Long? = null
		var y0: Long? = null
		var z0: Long? = null
		for ((i, h1) in hailstones.withIndex()) {
			for (h2 in hailstones.subList(i + 1, hailstones.size)) {
				if (isParallelXY(h1, h2)) {
					val stone = calcByParallelXYT(h1, h2, hailstones)
					if (stone != null) {
						//println("Parallel XY: $stone")
						x0 = stone.p.x.toLong()
						y0 = stone.p.y.toLong()
					}
				}
				if (isParallelXZ(h1, h2)) {
					val stone = calcByParallelXTZ(h1, h2, hailstones)
					if (stone != null) {
						//println("Parallel XZ: $stone")
						x0 = stone.p.x.toLong()
						z0 = stone.p.z.toLong()
					}
				}
				if (isParallelYZ(h1, h2)) {
					val stone = calcByParallelTYZ(h1, h2, hailstones)
					if (stone != null) {
						//println("Parallel YZ: $stone")
						y0 = stone.p.y.toLong()
						z0 = stone.p.z.toLong()
					}
				}
			}
		}
		if (x0 != null && y0 != null && z0 != null) return x0 + y0 + z0
		return bruteForce(hailstones)
	}

	val testInput = readInput("Day24_test")
	check(part1(testInput, 7L..27L) == 2)
	check(part2(testInput) == 47L)

	val input = readInput("Day24")
	part1(input).println()
	part2(input).println()
	// 13754
	// 711031616315001   (129723668686742, 353939130278484, 227368817349775), (312, -116, 109)
}
