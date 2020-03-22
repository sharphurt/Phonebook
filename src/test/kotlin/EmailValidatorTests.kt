@file:Suppress("DEPRECATION")

import com.sharphurt.phonebook.viewModel.ListModel
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailValidatorTests {
    val model = ListModel()

    @ParameterizedTest
    @ValueSource(
        strings = [
            "pasha@gmail.com",
            "pasha-100@gmail.com",
            "pasha.100@gmail.com",
            "pasha111@pasha.com",
            "pasha-100@pasha.net",
            "pasha.100@pasha.com.au",
            "pasha@gmail.com.com",
            "pasha+100@gmail.com"
        ]
    )
    fun checkAddressIsPassingVerification(number: String) {
        assertTrue(model.validateEmail(number))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "pasha",
            "pasha@.com.my",
            "pasha123@.com",
            "pasha123@.com.com",
            "pasha()*@gmail.com",
            "pasha@%*.com",
            "pasha@pasha@gmail.com"
        ]
    )
    fun checkAddressIsFailingVerification(number: String) {
        assertFalse(model.validateEmail(number))
    }
}
