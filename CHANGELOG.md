# 222.2.0

Added:

- Support for Rider 2022.2 

# 222.1.2

Fixed:

- Fix exceptions and ui crashing when rendering some comments
- Attempt to fix some typing issues

# 222.1.1

Fixed:

- Not processing comments with "p" tag
- Disable experimental features by default

# 1.0.0-EAP4

Fixed:

- Shifted comments' rendering inside collapsed fold regions
- Rendering quality in popups
- A bit incorrect rendering of invariants
- Some exceptions
- Forgotten unimplemented settings
- Incorrect color in table's borders and list's numbers/bullet
- Fixed not processing inheritdocs on class

# 1.0.0-EAP3

Fixed:

- Fixed too bright colors in todos and incorrect highlighting in some rendered comments
- Fixed re-highlight of all file when editing names of named entities
- Fixed initial settings: now comments are rendered instead of hiding
- Fixed not deleting old folding regions for comments in edit mode
- Fixed incorrect processing of multiple editors for single document
- Fixed not correct place of some settings

# 1.0.0-EAP2

Added:

- Setting "Use experimental features" to enable/disable experimental features 

Fixed:

- Performance when rendering comments in big files, such as Task.cs from standard library
- Not correctly highlighting langword reference
- Not correctly handling href reference
- Not that good UX when switching comment to edit mode

# 1.0.0-EAP1

Added:

- Functionality to render single-line, multiline, documentation comments and group of single-line comments
- Different modes of displaying comments: render mode, edit mode, hidden mode
- Settings to customize view of rendered comments, in which files to render comments
- Push-to-hint behaviour for rendered comments
- Action "Hide Or Render All Comments" to hide or render comments in current editor
- Experimental features to store implicit dependencies between entities in the code base
