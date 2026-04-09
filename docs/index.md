---
layout: page
title: Course Management System
---

[![Java CI](https://github.com/AY2526S2-CS2103T-F10-2/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2526S2-CS2103T-F10-2/tp/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/AY2526S2-CS2103T-F10-2/tp/graph/badge.svg?token=FXRR3PUODM)](https://codecov.io/gh/AY2526S2-CS2103T-F10-2/tp)

![Ui](images/Ui.png)

**Course Management System (CMS)** is a desktop application for **NUS CS course coordinators** who manage large student cohorts and tutor teams.

CMS combines a clean GUI with a fast, keyboard-first command workflow, so routine tasks such as adding, editing, searching, filtering, tagging, importing, and exporting records can be completed quickly and consistently.

## Why CMS?

Managing 500+ students and tutors with point-and-click tools can be slow and error-prone. CMS helps by providing:

* Fast command-based operations for common admin workflows
* Validation for key fields (e.g. NUS Matric, contact details)
* Uniqueness checks to reduce duplicate/conflicting records
* Flexible filtering and tagging for day-to-day coordination work

## Core capabilities

* Manage both student and tutor profiles in one place
* Add, edit, delete, find, list, filter, and sort contacts
* Tag records for custom grouping (e.g. `struggling`, `needs-help`)
* Import and export data for backup and transfer
* Mask/unmask sensitive fields during usage demos or screen sharing

## Documentation

* New to CMS? Start with the [**User Guide**](UserGuide.html#quick-start)
* Contributing or extending CMS? See the [**Developer Guide**](DeveloperGuide.html)
* Looking for source code and releases? Visit the [**GitHub repository**](https://github.com/AY2526S2-CS2103T-F10-2/tp)


**Acknowledgements**

* This codebase was evolved from [se-edu's AddressBook-Level3](https://github.com/se-edu/addressbook-level3).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)
