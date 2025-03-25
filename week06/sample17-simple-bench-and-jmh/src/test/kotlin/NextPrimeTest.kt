import pt.isel.nextPrime
import kotlin.test.Test
import kotlin.test.assertEquals

class NextPrimeTest {
    @Test
    fun testSmallNumber() {
        val p = nextPrime(254L)
        assertEquals(257L, p)
    }

    @Test
    fun testSmallNumberWhenIsPrime() {
        val p = nextPrime(1319L)
        assertEquals(1319L, p)
    }

    @Test
    fun testBigNumber1() {
        val p = nextPrime(10000000L)
        assertEquals(10000019L, p)
    }

    @Test
    fun testBigNumber2() {
        val p = nextPrime(1000000000L)
        assertEquals(1000000007L, p)
    }
}