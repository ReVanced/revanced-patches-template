<p align="center">
  <picture>
    <source
      width="256px"
      media="(prefers-color-scheme: dark)"
      srcset="assets/revanced-headline/revanced-headline-vertical-dark.svg"
    >
    <img 
      width="256px"
      src="assets/revanced-headline/revanced-headline-vertical-light.svg"
    >
  </picture>
  <br>
  <a href="https://revanced.app/">
     <picture>
         <source height="24px" media="(prefers-color-scheme: dark)" srcset="assets/revanced-logo/revanced-logo.svg" />
         <img height="24px" src="assets/revanced-logo/revanced-logo.svg" />
     </picture>
   </a>&nbsp;&nbsp;&nbsp;
   <a href="https://github.com/ReVanced">
       <picture>
           <source height="24px" media="(prefers-color-scheme: dark)" srcset="https://i.ibb.co/dMMmCrW/Git-Hub-Mark.png" />
           <img height="24px" src="https://i.ibb.co/9wV3HGF/Git-Hub-Mark-Light.png" />
       </picture>
   </a>&nbsp;&nbsp;&nbsp;
   <a href="http://revanced.app/discord">
       <picture>
           <source height="24px" media="(prefers-color-scheme: dark)" srcset="https://user-images.githubusercontent.com/13122796/178032563-d4e084b7-244e-4358-af50-26bde6dd4996.png" />
           <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032563-d4e084b7-244e-4358-af50-26bde6dd4996.png" />
       </picture>
   </a>&nbsp;&nbsp;&nbsp;
   <a href="https://reddit.com/r/revancedapp">
       <picture>
           <source height="24px" media="(prefers-color-scheme: dark)" srcset="https://user-images.githubusercontent.com/13122796/178032351-9d9d5619-8ef7-470a-9eec-2744ece54553.png" />
           <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032351-9d9d5619-8ef7-470a-9eec-2744ece54553.png" />
       </picture>
   </a>&nbsp;&nbsp;&nbsp;
   <a href="https://t.me/app_revanced">
      <picture>
         <source height="24px" media="(prefers-color-scheme: dark)" srcset="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
         <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
      </picture>
   </a>&nbsp;&nbsp;&nbsp;
   <a href="https://x.com/revancedapp">
      <picture>
         <source media="(prefers-color-scheme: dark)" srcset="https://user-images.githubusercontent.com/93124920/270180600-7c1b38bf-889b-4d68-bd5e-b9d86f91421a.png">
         <img height="24px" src="https://user-images.githubusercontent.com/93124920/270108715-d80743fa-b330-4809-b1e6-79fbdc60d09c.png" />
      </picture>
   </a>&nbsp;&nbsp;&nbsp;
   <a href="https://www.youtube.com/@ReVanced">
      <picture>
         <source height="24px" media="(prefers-color-scheme: dark)" srcset="https://user-images.githubusercontent.com/13122796/178032714-c51c7492-0666-44ac-99c2-f003a695ab50.png" />
         <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032714-c51c7492-0666-44ac-99c2-f003a695ab50.png" />
     </picture>
   </a>
   <br>
   <br>
   Continuing the legacy of Vanced
</p>

# 👋🧩 ReVanced Patches template

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/ReVanced/revanced-patches-template/release.yml)
![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)

Template repository for ReVanced Patches.

## ❓ About

This is a template to create a new ReVanced Patches repository.  
The repository can have multiple patches, and patches from other repositories can be used together.

For an example repository, see [ReVanced Patches](https://github.com/revanced/revanced-patches).

## 🚀 Get started

To start using this template, follow these steps:

1. [Create a new repository using this template](https://github.com/new?template_name=revanced-patches-template&template_owner=ReVanced)
2. Set up the [build.gradle.kts](patches/build.gradle.kts) file (Specifically, the [group of the project](patches/build.gradle.kts#L1),
and the [About](patches/build.gradle.kts#L5-L11))
3. Update dependencies in the [libs.versions.toml](gradle/libs.versions.toml) file
4. [Create a pass-phrased GPG master key and subkey](https://mikeross.xyz/create-gpg-key-pair-with-subkeys/)
   1. Add the private key as a secret named [GPG_PRIVATE_KEY](.github/workflows/release.yml#L52) to your repository
   2. Add the passphrase as a secret named [GPG_PASSPHRASE](.github/workflows/release.yml#L53) to your repository
   3. Add the fingerprint of the GPG subkey as a secret named [GPG_FINGERPRINT](.github/workflows/release.yml#L54) to your repository
5. Set up the [README.md](README.md) file[^1] (e.g, title, description, license, summary of the patches
that are included in the repository), the [issue templates](.github/ISSUE_TEMPLATE)[^2]  and the [contribution guidelines](CONTRIBUTING.md)[^3]

🎉 You are now ready to start creating patches!

[^1]: [Example README.md file](https://github.com/ReVanced/revanced-patches/blob/main/README.md)
[^2]: [Example issue templates](https://github.com/ReVanced/revanced-patches/tree/main/.github/ISSUE_TEMPLATE)
[^3]: [Example contribution guidelines](https://github.com/ReVanced/revanced-patches/blob/main/CONTRIBUTING.md)

## 🔘 Optional steps

You can also add the following things to the repository:

- API documentation, if you want to publish your patches as a library

## 🧑‍💻 Usage

To develop and release ReVanced Patches using this template, some things need to be considered:

- Development starts in feature branches. Once a feature branch is ready, it is squashed and merged into the `dev` branch
- The `dev` branch is merged into the `main` branch once it is ready for release
- Semantic versioning is used to version ReVanced Patches. ReVanced Patches have a public API for other patches to use
- Semantic commit messages are used for commits
- Commits on the `dev` branch and `main` branch are automatically released
via the [release.yml](.github/workflows/release.yml) workflow, which is also responsible for generating the changelog
and updating the version of ReVanced Patches. It is triggered by pushing to the `dev` or `main` branch.
The workflow uses the `publish` task to publish the release of ReVanced Patches
- The `buildAndroid` task is used to build ReVanced Patches so that it can be used on Android.
The `publish` task depends on the `buildAndroid` task, so it will be run automatically when publishing a release.

## 📚 Everything else

### 📙 Contributing

Thank you for considering contributing to ReVanced Patches template.  
You can find the contribution guidelines [here](CONTRIBUTING.md).

### 🛠️ Building

To build ReVanced Patches template,
you can follow the [ReVanced documentation](https://github.com/ReVanced/revanced-documentation).

## 📜 Licence

ReVanced Patches template is licensed under the GPLv3 licence.
Please see the [license file](LICENSE) for more information.
[tl;dr](https://www.tldrlegal.com/license/gnu-general-public-license-v3-gpl-3) you may copy, distribute
and modify ReVanced Patches template as long as you track changes/dates in source files.
Any modifications to ReVanced Patches template must also be made available under the GPL,
along with build & install instructions.
