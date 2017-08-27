# Prodigal Player Theme Help

## How to create customize theme

Themes for `Prodigal Music Player` are basically folders. Folder name is the theme name.

Each theme should contain a `config.json` file, which contains colors, shapes and other attributes. **All values‘ type must be string.**

Also, each theme folder should contain images for buttons. If not provided, the app will using the default images.

See below table for full supported attributes' names and valid values.

If you having troubles, please refer built-in themes.



| Key               | Value                             | Explain                                                          | Required                          |
| ----------------- | --------------------------------- | ----------------------------------------                         | --------------------------------- |
| next              | Image file name                   | Image for next button                                            | Yes                               |
| prev              | Image file name                   | Image for previous button                                        | Yes                               |
| menu              | Image file name                   | Image for menu button                                            | Yes                               |
| play              | Image file name                   | Image for play button                                            | Yes                               |
| wheel_outer       | Float as string                   | Outer bounds for wheel, max 1.0                                  | Yes                               |
| wheel_inner       | Float as string                   | Inner bounds for wheel, min 0.1                                  | Yes                               |
| wheel_color       | Wheel Color as string             | #AARRGGBB or #RRGGBB                                             | Yes                               |
| background_color  | Main background color as string   | #AARRGGBB or #RRGGBB                                             | Yes                               |
| text_color        | Text Color                        | #AARRGGBB or #RRGGBB                                             | Yes                               |
| item_color        | Color for highlighted items       | #AARRGGBB or #RRGGBB                                             | Yes                               |
| wheel_shape       | Wheel shape                       | Must be one of "rect", "oval", "polygon"                         | Yes                               |
| polygon_sides     | Polygon sides                     | Polygon sides when wheel_shape is "polygon". Must greater than 4 | Yes when wheel_shape is "polygon" |

## How to apply a theme

To apply a theme, upload it to your phone and go to Prodigal app settings -> themes -> and select your theme

## Troubleshoot

If you find your theme doesn't looks right, please kill APP and re-launch APP.

If your APP crashing after loaded customized theme, please delete that theme via computer or file manager app then re-launch APP.

If you met trouble when creating a new theme, please refer built-in themes.


