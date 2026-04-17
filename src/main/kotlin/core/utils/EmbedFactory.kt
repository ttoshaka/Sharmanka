package core.utils

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import core.models.Member
import net.dv8tion.jda.api.entities.MessageEmbed

/**
 * Фабрика для построения embed-сообщений Discord
 */
interface EmbedFactory {

    /**
     * Построить embed со списком всех доступных команд бота
     */
    fun buildHelpEmbed(): MessageEmbed

    /**
     * Построить embed с текущей очередью треков
     *
     * @param currentTrack текущий воспроизводимый трек, или null если ничего не играет
     * @param queue список треков в очереди
     * @param member участник сервера, запросивший команду
     */
    fun buildQueueEmbed(
        currentTrack: AudioTrack?,
        queue: List<AudioTrack>,
        member: Member,
    ): MessageEmbed

    /**
     * Построить embed с информацией о текущем воспроизводимом треке
     *
     * @param track текущий воспроизводимый трек
     * @param member участник сервера, запросивший команду
     */
    fun buildNowPlayingEmbed(
        track: AudioTrack,
        member: Member,
    ): MessageEmbed
}
