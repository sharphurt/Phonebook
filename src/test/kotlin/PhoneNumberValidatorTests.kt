@file:Suppress("DEPRECATION")

import com.sharphurt.phonebook.viewModel.ListModel
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PhoneNumberValidatorTests {
    val model = ListModel()

    @ParameterizedTest
    @ValueSource(
        strings = [
            "555-5555-555",
            "+79 504 203 260",
            "+79 (12) 504 203 260",
            "+79 (12) 504-203-260",
            "+79(12)504203260",
            "+7912504203260",
            "7912504203260"]
    )
    fun checkNumberIsPassingVerification(number: String) {
        assertTrue(model.validatePhoneNumber(number))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "+48 504 203 260@@",
            "+48.504.203.260",
            "+55(123) 456-78-90-",
            "+55(123) - 456-78-90",
            "504.203.260",
            " ",
            "-",
            "()",
            "() + ()",
            "(21 7777",
            "+48 (21)",
            "+"]
    )
    fun checkNumberIsFailingVerification(number: String) {
        assertFalse(model.validatePhoneNumber(number))
    }
}
