Current Workflow:
- AndroidManifest.xml -> LoginActivity: this file is where you specify the entry point to your 
application, here I made it LoginActivity instead of a MainActivity
- activity/LoginActivity: this is the main activity (screen) for the login functionality and it is 
comprised of 2 fragments: AccountFragment, LoginFragment
- fragment/AccountFragment: this handles the first initial login page (EditText and buttons)
- fragment/LoginFragment: this handles the first initial "new user" page (EditText and buttons)
- res/values/style: this specifies certain styles that can be used globally in the project
- res/values/strings: this creates string values that can be used globally in the project

TODO:
- buttons still aren't centered on the login or new user pages
- buttons don't all work (ie. take you to where they should)

IMPORTANT NOTES:
- Right now, the 'Create' button adds the current user to the DB, and the 'Delete' button deletes 
all entries from the DB. Prob should change it to delete a single entry. Also need to implement
the read and update buttons for 'this.currentUserId'
- When adding users and trying to log them ('READ'), search for "=>" this will show the pairs 
added to the collection
- It also takes a few seconds to load in the console after you click 'Create'