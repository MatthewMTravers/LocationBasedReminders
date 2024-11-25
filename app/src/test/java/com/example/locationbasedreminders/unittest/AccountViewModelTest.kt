package com.example.locationbasedreminders.unittest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.locationbasedreminders.model.AccountViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class AccountViewModelTest {
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var accountViewModel: AccountViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockFirestore = mock()
        mockCollectionReference = mock()
        accountViewModel = AccountViewModel(mockFirestore)
        whenever(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
    }

    @Test
    fun testCreateAccount_usernameAlreadyExists() {
        val mockCollectionReference: CollectionReference = mock()
        val mockQuery: Query = mock()
        val mockTask: Task<QuerySnapshot> = mock()
        val mockFirestore: FirebaseFirestore = mock {
            on { collection("users") } doReturn mockCollectionReference
        }

        whenever(mockCollectionReference.whereEqualTo("username", "testUser")).thenReturn(mockQuery)
        whenever(mockQuery.get()).thenReturn(mockTask)

        val mockQuerySnapshot: QuerySnapshot = mock {
            on { isEmpty } doReturn false
        }
        whenever(mockTask.addOnSuccessListener(any())).thenAnswer {
            val listener = it.getArgument<OnSuccessListener<QuerySnapshot>>(0)
            listener.onSuccess(mockQuerySnapshot)
            mockTask
        }

        val viewModel = AccountViewModel(db = mockFirestore)
        viewModel.createAccount("testUser", "password123", "password123")
        assertEquals("Username already exists!", viewModel.operationResult.value)
    }

    @Test
    fun testCreateAccount_accountCreationFails() {
        val mockCollectionReference: CollectionReference = mock()
        val mockQuery: Query = mock()
        val mockTaskGet: Task<QuerySnapshot> = mock()
        val mockTaskAdd: Task<DocumentReference> = mock()
        val mockFirestore: FirebaseFirestore = mock {
            on { collection("users") } doReturn mockCollectionReference
        }

        whenever(mockCollectionReference.whereEqualTo("username", "testUser")).thenReturn(mockQuery)
        whenever(mockQuery.get()).thenReturn(mockTaskGet)

        val mockQuerySnapshot: QuerySnapshot = mock {
            on { isEmpty } doReturn true
        }
        whenever(mockTaskGet.addOnSuccessListener(any())).thenAnswer {
            val listener = it.getArgument<OnSuccessListener<QuerySnapshot>>(0)
            listener.onSuccess(mockQuerySnapshot)
            mockTaskGet
        }

        whenever(mockCollectionReference.add(any())).thenReturn(mockTaskAdd)
        whenever(mockTaskAdd.addOnSuccessListener(any())).thenReturn(mockTaskAdd)
        whenever(mockTaskAdd.addOnFailureListener(any())).thenAnswer {
            val listener = it.getArgument<OnFailureListener>(0)
            listener.onFailure(Exception("Simulated Firestore error"))
            mockTaskAdd
        }

        val viewModel = AccountViewModel(db = mockFirestore)
        viewModel.createAccount("testUser", "password123", "password123")
        assertEquals("Error creating user: Simulated Firestore error", viewModel.operationResult.value)
    }


    @Test
    fun testCreateAccount_accountCreationSucceeds() {

        val mockQuery: Query = mock()
        val mockTaskGet: Task<QuerySnapshot> = mock()
        val mockTaskAdd: Task<DocumentReference> = mock()
        val mockFirestore: FirebaseFirestore = mock {
            on { collection("users") } doReturn mockCollectionReference
        }

        whenever(mockCollectionReference.whereEqualTo("username", "testUser")).thenReturn(mockQuery)
        whenever(mockQuery.get()).thenReturn(mockTaskGet)

        val mockQuerySnapshot: QuerySnapshot = mock {
            on { isEmpty } doReturn true
        }
        whenever(mockTaskGet.addOnSuccessListener(any())).thenAnswer {
            val listener = it.getArgument<OnSuccessListener<QuerySnapshot>>(0)
            listener.onSuccess(mockQuerySnapshot)
            mockTaskGet
        }

        val mockDocumentReference: DocumentReference = mock()
        whenever(mockCollectionReference.add(any())).thenReturn(mockTaskAdd)
        whenever(mockTaskAdd.addOnSuccessListener(any())).thenAnswer {
            val listener = it.getArgument<OnSuccessListener<DocumentReference>>(0)
            listener.onSuccess(mockDocumentReference)
            mockTaskAdd
        }
        whenever(mockTaskAdd.addOnFailureListener(any())).thenReturn(mockTaskAdd)

        val viewModel = AccountViewModel(db = mockFirestore)
        viewModel.createAccount("testUser", "password123", "password123")
        assertEquals("User created successfully with ID: ${viewModel.currentUserId}", viewModel.operationResult.value)
    }

}