# Simple Weight Tracker

[Japanese README](README.ja.md)

Simple Weight Tracker is a simple weight tracking app that records your net weight by subtracting the weight of your clothes from the weight shown on the scale.

There is no account registration or cloud sync. Your records are stored locally on your device.




https://github.com/user-attachments/assets/4aeaeb73-0f24-4d22-8c33-c691ad33eaa4



## Features

- Add weight records
- View saved records in a list
- Edit and delete records
- View daily weight trends in a graph
- Show a moving average line
- Show the weight change trend
- Change graph line colors
- Change the moving average period
- Export records as CSV
- View licenses for used libraries

## App Overview

This app does not simply save the value shown on the scale. Instead, it subtracts the weight of your clothes and stores the result as your net weight.

```text
Net weight = Scale weight - Clothes weight
```

For example, if the scale shows `70.2 kg` and your clothes weigh `0.8 kg`, the app records your net weight as `69.4 kg`.

## Screens

### Record

This screen is used to add and edit weight records.

The input fields are:

- Date
- Scale weight
- Clothes weight

When adding a new record, today's date is set by default.

The clothes weight is carried over from the most recent record. If there is no previous record, it defaults to `0`.

Before saving, you can preview the calculated net weight.

### List

Saved weight records are displayed as cards.

Each record shows:

- Date
- Updated time
- Net weight

Each record can be edited or deleted.

A confirmation dialog is shown before deletion to help prevent accidental deletes.

### Graph

You can view daily weight changes in a graph.

The graph shows:

- A line graph of net weight
- A moving average line
- A weight change trend

The graph range can be selected from:

- 1 month
- 3 months
- 6 months
- 1 year
- All

If multiple records exist on the same day, the graph uses the lowest net weight for that day.

The moving average period can be changed from the settings screen. The default is `7 days`.

The weight change trend is calculated from record data using linear regression and displayed as the change per month.

### Settings

The settings screen lets you configure display and export options.

Main settings include:

- Color settings
- Moving average period
- CSV export
- License view

In color settings, you can change the colors of:

- The net weight line
- The moving average line

## CSV Export

Record data can be exported as a CSV file.

When exporting CSV, a confirmation dialog is shown. Pressing OK creates the CSV file.

The exported CSV contains the following columns:

```csv
date,measuredWeight,clothesWeight,netWeight
```

Column meanings:

| Column | Description |
|---|---|
| `date` | Record date |
| `measuredWeight` | Weight shown on the scale |
| `clothesWeight` | Clothes weight |
| `netWeight` | Net weight after subtracting clothes weight |

## Privacy

Simple Weight Tracker does not use account registration or cloud sync.

Record data is stored locally on your device.

## Tech Stack

- Kotlin
- Jetpack Compose
- Room
- Material Design

## License

See the `LICENSE` file for this repository's license.

## Notes

This app is intended to support personal weight tracking.

It is not a medical app.
