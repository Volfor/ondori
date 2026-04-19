package com.volfor.ondori.features.alarm.data.mappers

import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmEntity
import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmSoundMode
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class AlarmMapperTest {

    private val sampleRepeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)

    private fun sampleEntity(
        id: Long = 42L,
        hour: Int = 7,
        minute: Int = 5,
        repeatDays: Set<DayOfWeek> = sampleRepeatDays,
        enabled: Boolean = true,
        label: String? = "Wake up",
        soundMode: AlarmSoundMode = AlarmSoundMode.DEFAULT,
        soundUri: String? = null,
    ) = AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
        enabled = enabled,
        label = label,
        soundMode = soundMode,
        soundUri = soundUri,
    )

    private fun sampleAlarm(
        id: Long = 42L,
        hour: Int = 7,
        minute: Int = 5,
        repeatDays: Set<DayOfWeek> = sampleRepeatDays,
        enabled: Boolean = true,
        label: String? = "Wake up",
        sound: AlarmSound = AlarmSound.Default,
    ) = Alarm(
        id = id,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
        enabled = enabled,
        label = label,
        sound = sound,
    )

    @Test
    fun `toDomain maps CUSTOM with null uri to Default sound`() {
        val entity = sampleEntity(soundMode = AlarmSoundMode.CUSTOM, soundUri = null)
        assertEquals(AlarmSound.Default, entity.toDomain().sound)
    }

    @Test
    fun `toDomain maps CUSTOM with blank uri to Default sound`() {
        val blanks = listOf("", " ", "  ", "\t", "\n")
        for (uri in blanks) {
            val entity = sampleEntity(soundMode = AlarmSoundMode.CUSTOM, soundUri = uri)
            assertEquals("uri=$uri", AlarmSound.Default, entity.toDomain().sound)
        }
    }

    @Test
    fun `toDomain maps CUSTOM with non-blank uri to Custom`() {
        val uri = "content://media/external/audio/media/1"
        val entity = sampleEntity(soundMode = AlarmSoundMode.CUSTOM, soundUri = uri)
        assertEquals(AlarmSound.Custom(uri), entity.toDomain().sound)
    }

    @Test
    fun `toDomain maps DEFAULT to Default regardless of stored uri`() {
        val entity = sampleEntity(
            soundMode = AlarmSoundMode.DEFAULT,
            soundUri = "content://ignored",
        )
        assertEquals(AlarmSound.Default, entity.toDomain().sound)
    }

    @Test
    fun `toDomain maps SILENT to Silent regardless of stored uri`() {
        val entity = sampleEntity(
            soundMode = AlarmSoundMode.SILENT,
            soundUri = "content://ignored",
        )
        assertEquals(AlarmSound.Silent, entity.toDomain().sound)
    }

    @Test
    fun `toLocal maps Default to DEFAULT mode and null uri`() {
        val alarm = sampleAlarm(sound = AlarmSound.Default)
        val entity = alarm.toLocal()
        assertEquals(AlarmSoundMode.DEFAULT, entity.soundMode)
        assertEquals(null, entity.soundUri)
    }

    @Test
    fun `toLocal maps Silent to SILENT mode and null uri`() {
        val alarm = sampleAlarm(sound = AlarmSound.Silent)
        val entity = alarm.toLocal()
        assertEquals(AlarmSoundMode.SILENT, entity.soundMode)
        assertEquals(null, entity.soundUri)
    }

    @Test
    fun `toLocal maps Custom to CUSTOM mode and uri`() {
        val uri = "file:///sdcard/Music/alarm.mp3"
        val alarm = sampleAlarm(sound = AlarmSound.Custom(uri))
        val entity = alarm.toLocal()
        assertEquals(AlarmSoundMode.CUSTOM, entity.soundMode)
        assertEquals(uri, entity.soundUri)
    }

    @Test
    fun `toDomain copies alarm fields except sound mapping`() {
        val entity = sampleEntity(
            id = 99L,
            hour = 23,
            minute = 59,
            repeatDays = setOf(DayOfWeek.SUNDAY),
            enabled = false,
            label = null,
            soundMode = AlarmSoundMode.SILENT,
        )
        val domain = entity.toDomain()
        assertEquals(99L, domain.id)
        assertEquals(23, domain.hour)
        assertEquals(59, domain.minute)
        assertEquals(setOf(DayOfWeek.SUNDAY), domain.repeatDays)
        assertEquals(false, domain.enabled)
        assertEquals(null, domain.label)
        assertEquals(AlarmSound.Silent, domain.sound)
    }

    @Test
    fun `round-trip domain to entity to domain for Default`() {
        val original = sampleAlarm(sound = AlarmSound.Default)
        assertEquals(original, original.toLocal().toDomain())
    }

    @Test
    fun `round-trip domain to entity to domain for Silent`() {
        val original = sampleAlarm(sound = AlarmSound.Silent)
        assertEquals(original, original.toLocal().toDomain())
    }

    @Test
    fun `round-trip domain to entity to domain for Custom`() {
        val original = sampleAlarm(sound = AlarmSound.Custom("content://a/b"))
        assertEquals(original, original.toLocal().toDomain())
    }

    @Test
    fun `round-trip entity to domain to entity for DEFAULT with null uri`() {
        val original = sampleEntity(soundMode = AlarmSoundMode.DEFAULT, soundUri = null)
        assertEquals(original, original.toDomain().toLocal())
    }

    @Test
    fun `round-trip entity to domain to entity for SILENT with null uri`() {
        val original = sampleEntity(soundMode = AlarmSoundMode.SILENT, soundUri = null)
        assertEquals(original, original.toDomain().toLocal())
    }

    @Test
    fun `round-trip entity to domain to entity for CUSTOM with non-blank uri`() {
        val original = sampleEntity(
            soundMode = AlarmSoundMode.CUSTOM,
            soundUri = "https://example.com/sound",
        )
        assertEquals(original, original.toDomain().toLocal())
    }

    @Test
    fun `round-trip normalizes CUSTOM with null uri to DEFAULT row`() {
        val broken = sampleEntity(soundMode = AlarmSoundMode.CUSTOM, soundUri = null)
        val normalized = sampleEntity(soundMode = AlarmSoundMode.DEFAULT, soundUri = null)
        assertEquals(normalized, broken.toDomain().toLocal())
    }

    @Test
    fun `round-trip normalizes CUSTOM with blank uri to DEFAULT row`() {
        val broken = sampleEntity(soundMode = AlarmSoundMode.CUSTOM, soundUri = "   ")
        val normalized = sampleEntity(soundMode = AlarmSoundMode.DEFAULT, soundUri = null)
        assertEquals(normalized, broken.toDomain().toLocal())
    }

    @Test
    fun `round-trip drops uri when mode is DEFAULT`() {
        val stored = sampleEntity(
            soundMode = AlarmSoundMode.DEFAULT,
            soundUri = "content://orphan",
        )
        val expected = sampleEntity(
            soundMode = AlarmSoundMode.DEFAULT,
            soundUri = null,
        )
        assertEquals(expected, stored.toDomain().toLocal())
    }

    @Test
    fun `round-trip drops uri when mode is SILENT`() {
        val stored = sampleEntity(
            soundMode = AlarmSoundMode.SILENT,
            soundUri = "content://orphan",
        )
        val expected = sampleEntity(
            soundMode = AlarmSoundMode.SILENT,
            soundUri = null,
        )
        assertEquals(expected, stored.toDomain().toLocal())
    }

    @Test
    fun `list extension maps each entity`() {
        val entities = listOf(
            sampleEntity(id = 1L, soundMode = AlarmSoundMode.DEFAULT),
            sampleEntity(id = 2L, soundMode = AlarmSoundMode.SILENT),
        )
        val domains = entities.toDomain()
        assertEquals(2, domains.size)
        assertEquals(AlarmSound.Default, domains[0].sound)
        assertEquals(AlarmSound.Silent, domains[1].sound)
    }
}
