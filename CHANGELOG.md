# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.8.1] - 2026-07-15
### Changed
- Desparasitacion filter now shows animals pending to desparasitacion

---

## [1.8.0] - 2026-07-09
### Added
- Avatar logic and controllers

---

## [1.7.0] - 2026-07-03
### Changed
- Simplified animal filtering criteria

---

## [1.6.0] - 2025-10-05
### Changed
- Remove "api" from controllers

---

## [1.5.2] - 2025-10-04
### Changed
- Increase file size to 50MB

---

## [1.5.1] - 2025-09-14
### Fixed
- Fix ultimo peso value
- Allow localizador to be null when animal is inactive
- Fix password request body
- Fix new user id value

---

## [1.5.0] - 2025-09-14
### Added
- Update, delete and update password endpoints for users

---

## [1.4.0] - 2025-09-06
### Added
- New estado "Propietario" for both types of animals

### Changed
- Localizacion is now dynamic and can be set by the user

---

## [1.3.0] - 2025-08-28
### Added
- Search box also searches by "enfermedades"

### Changed
- "Nombre" is the default sorting parameter

---

## [1.2.1] - 2025-08-28

### Fixed
- Field "descripcion" in historial entity marked as longtext to accommodate larger text entries

---

## [1.2.0] - 2025-08-28
### Added
- Added pagination support for animal listing endpoint

### Changed
- 

---

## [1.1.0] - 2025-08-27
### Added
- Soft delete / active toggle for animals
- Add fecha creacion y fecha modificacion to animal model

### Changed
- 

---

## [1.0.0] - 2025-08-21
### Added
- Initial vetlliga release
