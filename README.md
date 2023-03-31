<p align="center">
    <img src="assets/Sketchware-Pro.png" />
</p>

# Sketchware Pro

*Read this in other languages: [English](README.md), [Português](README-PT.md).*

![GitHub contributors](https://img.shields.io/github/contributors/Sketchware-Pro/Sketchware-Pro) ![GitHub last commit](https://img.shields.io/github/last-commit/Sketchware-Pro/Sketchware-Pro) ![Discord server stats](https://img.shields.io/discord/790686719753846785)
[![Android CI](https://github.com/DereckySany/Sketchware-Pro-aapt1_aapt2_multilib/actions/workflows/android.yml/badge.svg)](https://github.com/DereckySany/Sketchware-Pro-aapt1_aapt2_multilib/actions/workflows/android.yml)

Here you'll find the source code of many classes in Sketchware Pro, and most importantly, **the
place** to contribute to Sketchware Pro.

## Building the app

You must use Gradle to build the app. It's highly recommended to use Android Studio, though.

There are two build variants with different features:

 - `minApi26:` Supports exporting AABs from projects, as well as compiling Java 1.8, 1.9, 10, and 11 code.
However, it **only works on Android 8.0 (O) and above**.
 - `minApi21:` Can't produce AABs from projects, and can only compile Java 1.7 code, but it supports down to Android 5.

You must select the appropriate build variant in Android Studio using the Build Variants tab
or use the appropriate build Gradle command.

### Source code map

*Some classes are not available in all versions of the code, as they may have been replaced or not
used in the version!*
| Class | Description |
|---|---|
| [`a.a.a.Dp`](app/src/main/java/a/a/a/Dp.java) | Helper to compile an entire project |
| [`a.a.a.aB`](app/src/main/java/a/a/a/aB.java) | Audio File Manager |
| [`a.a.a.bB`](app/src/main/java/a/a/a/bB.java) | Video File Manager |
| `a.a.a.Boi` | Responsible for generating the XML files of the layouts |
| [`a.a.a.dt`](app/src/main/java/a/a/a/dt.java) | Responsible for resource processing |
| [`a.a.a.Fw`](app/src/main/java/a/a/a/Fw.java) | Application Task Management Helper |
| `a.a.a.GC` | Image File Manager |
| [`a.a.a.Gx`](app/src/main/java/a/a/a/Gx.java) | Responsible for generating resources and compiling
projects |
| [`a.a.a.gt`](app/src/main/java/a/a/a/gt.java) | Library Resource Management Helper |
| [`a.a.a.Hx`](app/src/main/java/a/a/a/Hx.java) | Helper for project code execution |
| [`a.a.a.Ix`](app/src/main/java/a/a/a/Ix.java) | Responsible for AndroidManifest.xml generation |
| [`a.a.a.jC`](app/src/main/java/a/a/a/jC.java) | Project File Manager |
| [`a.a.a.Jp`](app/src/main/java/a/a/a/Jp.java) | Project Theme Manager |
| [`a.a.a.jq`](app/src/main/java/a/a/a/jq.java) | Project Template Manager |
| [`a.a.a.jr`](app/src/main/java/a/a/a/jr.java) | Responsible for managing library resources |
| [`a.a.a.Jx`](app/src/main/java/a/a/a/Jx.java) | Responsible for generating the source code of the
activities |
| [`a.a.a.Kp`](app/src/main/java/a/a/a/Kp.java) | Text File Manager |
| [`a.a.a.Lx`](app/src/main/java/a/a/a/Lx.java) | Source code generator for components such as
listeners etc. |
| [`a.a.a.MB`](app/src/main/java/a/a/a/MB.java) | Backup File Manager |
| [`a.a.a.mq`](app/src/main/java/a/a/a/mq.java) | Library File Manager |
| [`a.a.a.Mx`](app/src/main/java/a/a/a/Mx.java) | Package File Manager |
| `a.a.a.Nx` | File Management Helper |
| [`a.a.a.oq`](app/src/main/java/a/a/a/oq.java) | Theme Image File Manager |
| [`a.a.a.Ox`](app/src/main/java/a/a/a/Ox.java) | HTML File File Manager |
| [`a.a.a.qA`](app/src/main/java/a/a/a/qA.java) | Theme Manifest File Manager |
| [`a.a.a.qq`](app/src/main/java/a/a/a/qq.java) | Registry of internal library dependencies |
| [`a.a.a.rs`](app/src/main/java/a/a/a/rs.java) | Library Management Assistant |
| [`a.a.a.sB`](app/src/main/java/a/a/a/sB.java) | Theme File Manager |
| [`a.a.a.sq`](app/src/main/java/a/a/a/sq.java) | Responsible for generating content provider source
code |
| [`a.a.a.tq`](app/src/main/java/a/a/a/tq.java) | Responsible for compiling dialogue questionnaires
|
| [`a.a.a.tx`](app/src/main/java/a/a/a/tx.java) | Animation File Manager |
| [`a.a.a.uq`](app/src/main/java/a/a/a/uq.java) | Font File Manager |
| [`a.a.a.Ws`](app/src/main/java/a/a/a/Ws.java) | Plain Text File Manager |
| [`a.a.a.wB`](app/src/main/java/a/a/a/wB.java) | SVG Image File Manager |
| [`a.a.a.wq`](app/src/main/java/a/a/a/wq.java) | Image File Manager |
| [`a.a.a.xo`](app/src/main/java/a/a/a/xo.java) | Vector Image File Manager |
| [`a.a.a.yq`](app/src/main/java/a/a/a/yq.java) | Arrange File Paths in Sketchware Projects |
| [`a.a.a.ZA`](app/src/main/java/a/a/a/ZA.java) | Resource Caching Manager |

* ***more in [`Classe Index`](ClassIndex.md).***

You can also check the [`mod`](app/src/main/java/mod) package that has the majority of contributors'
changes.

## Contributing

Fork this repository and contribute back using
[`pull requests`](https://github.com/Sketchware-Pro/Sketchware-Pro/pulls).

Any contributions, large or small, major features, or bug fixes, are welcomed and appreciated, but
will
be thoroughly reviewed.

### How to contribute

- Fork the repository to your GitHub account.
- Make a branch if necessary.
- Clone the forked repository to your local device (optional, you can edit files through GitHub's web interface).
- Make changes to files.
- (IMPORTANT) Test out those changes.
- Create a pull request in this repository.
- The repository members will review your pull request, and merge it when they are accepted.

### What changes we'll (most likely) not accept

Most changes might be UI-related, and we think it's more or less a waste of time. If something design-related gets changed,
ideally the whole app must follow the new style too, and that's hard to accomplish, especially for mods. That's why:

- Major changes to the UI (components which exist in vanilla Sketchware) are unlikely to be accepted.

### Commit message

When you've made changes to one or more files, you have to *commit* that file. You also need a
*message* for that *commit*.

You should read [these guidelines](https://www.freecodecamp.org/news/writing-good-commit-messages-a-practical-guide/), or that summarized:

- Short and detailed.
- Prefix one of these commit types:
   - `feat:` A feature, possibly improving something already existing.
   - `fix:` A fix, for example of a bug.
   - `style:` Feature and updates related to styling.
   - `refactor:` Refactoring a specific section of the codebase.
   - `test:` Everything related to testing.
   - `docs:` Everything related to documentation.
   - `chore:` Code maintenance (you can also use emojis to represent commit types).

Examples:
 - `feat: Speed up compiling with new technique`
 - `fix: Fix crash during launch on certain phones`
 - `refactor: Reformat code at File.java`


## Thanks for contributing
They help keeping Sketchware Pro alive. Each (helpful) accepted contribution will get noted down in the "About Modders" activity. We'll use your GitHub name and profile picture initially, but they can be
changed of course.

## Discord
Wanna chat with us, talk about changes, or just hang out? We have a Discord server just for that.

[![Join our Discord server!](https://invidget.switchblade.xyz/kq39yhT4rX)](http://discord.gg/kq39yhT4rX)

## Disclaimer
This mod was not meant for any harmful purposes, such as harming Sketchware; Quite the opposite actually.
It was made to keep Sketchware alive by the community for the community. Please use it at your own discretion
and be a Patreon backer of them, for example. Sadly, all other ways to support them aren't working anymore,
so it's the only way available currently.
[Here's their Patreon page.](https://www.patreon.com/sketchware)

We love Sketchware very much, and we are grateful to Sketchware's developers for making such an amazing app, but unfortunately, we haven't received updates for a long time.
That's why we decided to keep Sketchware alive by making this mod, plus we don't demand any money, it's completely free :)
