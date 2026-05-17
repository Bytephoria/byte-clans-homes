# ByteClans Homes

<p align="center">
  <a href="https://www.java.com/">
    <img src="https://img.shields.io/badge/Java-21+-blue" alt="Java"/>
  </a>
  <a href="https://papermc.io/">
    <img src="https://img.shields.io/badge/PaperMC-1.21.1%2B-green" alt="PaperMC"/>
  </a>
  <a href="license">
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/>
  </a>
  <a href="https://discord.com/invite/3K9yrZQRmS">
    <img src="https://img.shields.io/discord/1350369915521204276?label=Discord&color=7289DA&logo=discord&logoColor=white" alt="Discord"/>
  </a>
</p>

**ByteClans Homes** is an official addon for ByteClans that adds a complete clan home system with support for multiple homes, default homes, teleport delays and configurable limits.

---

## Overview

Allows clans to create and manage shared homes directly through ByteClans commands.

Homes are stored directly inside clan data containers and support automatic default-home assignment, teleport countdowns and configurable limits.

Designed to stay lightweight while integrating naturally with the existing ByteClans ecosystem.

---

## Features

- Multiple homes per clan.
- Configurable home limit.
- Default clan home support.
- Automatic first-home default assignment.
- Teleport countdown system.
- Home renaming support.
- Home deletion and automatic cleanup.
- Fully configurable messages.
- Suggestions/autocomplete support.
- Data stored directly in clan containers.

---

## Commands

| Command | Description |
|---|---|
| `/clan home` | Teleport to the default clan home |
| `/clan home list` | View clan homes |
| `/clan home set <name>` | Create a clan home |
| `/clan home tp <name>` | Teleport to a specific home |
| `/clan home default <name>` | Set the default home |
| `/clan home rename <old> <new>` | Rename a home |
| `/clan home delete <name>` | Delete a home |

---

## Configuration

```yaml
settings:
  teleport-countdown: 5 # Seconds to wait before teleporting
  limit: 3 # Maximum number of homes a clan can have
  auto-default: true # Automatically sets the first created home as default
```

---

## Dependencies

Required:

- ByteClans
- Paper 1.21.1+
- Java 21+

---

## Contributing

1. Fork the repository.
2. Create a branch:

```bash
git checkout -b feature/my-feature
```

or

```bash
git checkout -b fix/my-fix
```

3. Commit changes and open a Pull Request.

Please follow the existing code style:

- Use `this.` for instance fields.
- Use `final` whenever possible.
- Avoid unnecessary allocations.
- Keep data access asynchronous whenever required.
- Maintain consistency with ByteClans APIs.

---

## License

This project is released under the [MIT License](LICENSE).  
You are free to use, modify, and distribute it with attribution.