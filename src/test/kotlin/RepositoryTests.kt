@file:Suppress("DEPRECATION")

import com.sharphurt.phonebook.data.PeopleRepository
import com.sharphurt.phonebook.domain.Person
import junit.framework.Assert.*
import org.junit.Test

import java.io.File

class RepositoryTests {
    private val repository = PeopleRepository("testPhonebook.json")

    @Test
    fun savingPeopleList() {
        val savingPeopleList = arrayListOf(
            Person("abc", "123", "def"),
            Person("test", "1233494", "testing"),
            Person("gorod", "98765", "test")
        )
        repository.savePeople(savingPeopleList)
        assertEquals(savingPeopleList, repository.getPeople())
    }

    @Test
    fun savingToEmptyFile() {
        val savingPeopleList = arrayListOf(
            Person("abc", "123", "def")
        )
        File(repository.path).writeText("")
        repository.savePeople(savingPeopleList)
        val savingPeopleList1 = arrayListOf(
            Person("bcde", "45678", "null"),
            Person("test", "1233494", "testing"),
            Person("gorod", "98765", "test")
        )
        repository.savePeople(savingPeopleList1)
        assertEquals(savingPeopleList1, repository.getPeople())
    }

    @Test
    fun gettingFromDefectiveFile() {
        val savingPeopleList = arrayListOf(
            Person("bcde", "45678", "null"),
            Person("test", "1233494", "testing"),
            Person("gorod", "98765", "test")
        )
        repository.savePeople(savingPeopleList)
        File(repository.path).writeText(File(repository.path).readText().removeRange(3, 9))
        File(repository.path).appendText("string for damaging file")
        assertEquals(ArrayList<Person>(), repository.getPeople())
        File(repository.path).delete()
    }

}
