# Contributing to GBU Timetables

Thanks for your interest in this project. Many guidlines here were originally adopted
from the Eclipse OMR project.

We welcome and enourage all kinds of contribution to the project, not only code.
This includes bug reports, user experience feedback, assistance in reproducing
issues and more.

## Getting started with GBU Timetables developemt

### Introduction

GBU Timetables in an android application to view timetables for both faculty and students.  
https://play.google.com/store/apps/details?id=com.varun.gbu_timetables .  
It is recommended that you install the application from play store and understand
all its features as explained in the [README](README.md)

### Introduction to Android Development

Start with Android tutorials on [Udacity](https://in.udacity.com/course/new-android-fundamentals--ud851).  
It is very beginning friendly and will help you with application code. 
It's very big and I don't expect you to complete it, but at least go half way through it to understand code here.

## Code structure
Inside the directory `app/src/main/java/com/varun/gbu_timetables`

<pre>
├── AboutActivity.java <b>: Credits</b>
├── adaptor <b>: Adapters for custom BaseExpandableListAdapter and ArrayAdapter</b>
│   ├── FavouritesAdapter.java <b>: Extended ArrayAdapter for favourites list</b>
│   ├── SectionsFacultyAdapter.java <b>: Extended BaseExpandableListAdapter for both faculty and sections expandable lists</b>
│   └── TimetableAdapter.java <b>: Our own adapter for viewing timetables in both single page and day wise modes</b>
├── AppCompatPreferenceActivity.java <b>: Boilerplate code</b>
├── asyncTask
│   └── UpdateDatabaseOnlineTask.java <b>: Update database from <a>http://gbuonline.in</a></b>
├── CourseStructureFragment.java <b>: ICT Course structure, now removed!</b>
├── CourseStructure.java <b>: ICT Course structure, now removed!</b>
├── data <b>: All data comes from here</b>
│   ├── Database <b>: Files in this directory are in same format same as Udacity course</b>
│   │   ├── TimetableContract.java
│   │   ├── TimetableDbHelper.java
│   │   └── TimetableProvider.java <b>: Our Content Provider</b>
│   ├── MD5.java <b>: MD5 snippet from <s>Cyanongemod</s> Lineage </b>
│   └── Model <b>: CSF: Course Structure Faculty</b>
│       ├── CSF_FAC_MAP_KEY.java
│       ├── CSF.java
│       ├── PairKey.java
│       └── TimeTableBasic.java
├── DetailsAdapter.java <b>: Adapter for additional Faculty / Section information displayed on bottom while browsing timetables</b>
├── FacultyFragment.java <b>: Faculty list, used faculty adapter</b>
├── FavouritesFragment.java <b>: Favourites list, uses favourites adapter</b>
├── MainActivity.java <b>: Main Activivity, called after splash screen</b>
├── SectionsFragment.java <b>: Sections list, uses Sections Adapter</b>
├── service <b>: Background services</b>
│   ├── FirebaseNotificationReceiver.java <b>: Custom firebase notification reciever</b>
│   ├── MyFirebaseInstanceIdService.java <b>: Boilerplate code</b>
│   └── UpdateDatabaseService.java <b>: Update database in background: Not working</b>
├── SettingsActivity.java <b>: Settings Screen</b>
├── SplashScreenActivity.java <b>: Splash Screen</b>
├── TimetableActivity.java <b>: Timetable Activity that includes both: </b>
├── TimetableFragmentPager.java <b>: Day wise</b>
├── TimetableFragmentSinglePage.java <b>: Single Page</b>
└── Utility.java <b>: Static methods that can be used everywhere</b>
</pre>

#### Notes
1. Please try to make sure changes are as backward compatible as possible.  
With some additional code, app was working fine with Android version 2.1,
but with firebase I needed to move to android 2.3 and after updating 
some libraries lately, it needs Android 4.0.  
So please contribute code that is backward compatible with 4.0 (API Level: 14)

2. Please don't copy paste code without crediting original authors.
Include there names in commit messages and in code comments, and I 
will find appropriate place to add them in application.

3. Follow commit guidelines as listed in this page.

## Issues

This project uses GitHub Issues to track ongoing development, discuss project
plans, and keep track of bugs.  Be sure to search for existing issues before
you create another one.

## Submitting a contribution

You can propose contributions by sending pull requests through GitHub.
Following these guidelines will help us to merge your pull requests smoothly:

1. If you're not sure your contribution would be accepted, and want to validate
   your approach or idea before writing code, feel free to open an issue. However,
   not every feature or fix needs an issue. If the problem and fix are cleanly
   connected, and you have the fix in hand, feel free to just submit a pull request.

2. Your pull request is an opportunity to explain both what changes you'd like
   pulled in, but also _why_ you'd like them added. Providing clarity on why
   you want changes makes it easier to accept, and provides valuable context to
   review.

3. Follow the coding style and format of the code you are modifying. 
   The code base is yet to be unified in style however, so if the 
   file you are editing seems to have a diffferent
   style, defer to the style of the file as you found it.

4. Follow the commit guidelines found below.

5. We encourage you to open a pull request early, and mark it as "Work In Progress"
   (prefix the PR title with WIP). This allows feedback to start early, and helps
   create a better end product. Committers will wait until after you've removed
   the WIP prefix to merge your changes.

# Commit Guidelines

The first line describes the change made. It is written in the imperative mood,
and should say what happens when the patch is applied. Keep it short and
simple. The first line should be less than 70 characters, where reasonable,
and should be written in sentence case preferably not ending in a period.
Leave a blank line between the first line and the message body.

The body should be wrapped at 72 characters, where reasonable.

Include as much information in your commit as possible. You may want to include
designs and rationale, examples and code, or issues and next steps. Prefer
copying resources into the body of the commit over providing external links.
Structure large commit messages with headers, references etc. Remember however
that the commit message is always going to be rendered in plain text.

Please add `[skip ci]` to the commit message when the change doesn't require a
compilation, such as documentation only changes, to avoid unnecessarily wasting
the project's build resources.

Use the commit footer to place commit metadata. The footer is the last block of
contiguous text in the message. It is separated from the body by one or more
blank lines, and as such cannot contain any blank lines. Lines in the footer are
of the form:

```
Key: Value
```

When a commit has related issues or commits, explain the relation in the message
body. You should also leave an `Issue` tag in the footer. However, if this is the
final commit that fixes an issue you should leave a `Closes` tag (or one of the
tags described [here](https://help.github.com/articles/closing-issues-using-keywords/)
in the footer instead which will automatically close the referenced issue when
this pull request is merged.

For example, if this is just one of the commits necessary to address Issue 1234
then the following is a valid commit message:

```
Correct race in frobnicator

This patch eliminates the race condition in issue #1234.

Issue: #1234
```

However, if this is the final commit that addresses the issue then the following
is an acceptable commit message:

```
Correct race in frobnicator

This patch eliminates the race condition in issue #1234.

Closes: #1234
```

Sign off on your commit in the footer. By doing this, you assert original
authorship of the commit and that you are permitted to contribute it. This can
be automatically added to your commit by passing `-s` to `git commit`, or by
hand adding the following line to the footer of the commit.

```
Signed-off-by: Full Name <email>
```

Remember, if a blank line is found anywhere after the `Signed-off-by` line, the
`Signed-off-by:` will be considered outside of the footer, and will fail the
automated Signed-off-by validation.

It is important that you read and understand the legal considerations found
below when signing off or contributing any commit.

### Example commits

Here is an example of a *good* commit:

```
Update and expand the commit guidelines

Elaborate on the style guidelines for commit messages. These new
style guidelines reflect the conversation found in #124.

The guidelines are changed to:
- Provide guidance on how to write a good first line.
- Elaborate on formatting requirements.
- Relax the advice on using issues for nontrivial commits.
- Move issue references from the first line to the message footer.
- Encourage contributors to put more information into the commit
  message.

Issue: #124
Signed-off-by: Robert Young <rwy0717@gmail.com>
```

The first line is meaningful and imperative. The body contains enough
information that the reader understands the why and how of the commit, and its
relation to any issues. The issue is properly tagged and the commit is signed
off.

The following is a *bad* commit:

```
FIX #124: Changing a couple random things in CONTRIBUTING.md.
Also, there are some bug fixes in the thread library.
```

The commit rolls unrelated changes together in a very bad way. There is not
enough information for the commit message to be useful. The first line is not
meaningful or imperative. The message is not formatted correctly, the issue is
improperly referenced, and the commit is not signed off by the author.

### Other resources for writing good commits

- http://chris.beams.io/posts/git-commit/