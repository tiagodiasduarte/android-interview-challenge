package pt.tiagoduarte.challenge.ui.theme

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices

@Retention(AnnotationRetention.SOURCE)
@Preview(name = "Pixel Tablet", device = Devices.PIXEL_TABLET)
@Preview(name = "Pixel 9 Pro", device = Devices.PIXEL_9_PRO_XL)
@Preview(name = "Nexus 5", device = Devices.NEXUS_5)
annotation class PreviewDevices
