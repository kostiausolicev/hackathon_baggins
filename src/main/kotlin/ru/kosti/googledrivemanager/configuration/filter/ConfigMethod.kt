package ru.kosti.googledrivemanager.configuration.filter

import ru.kosti.googledrivemanager.enumeration.Roles


/**
 * ConfigMethod class contains information about route security filter
 * @property role is the minimum resolution
 * @property verified is the flag, which shows the need for token verification
 */
class ConfigMethod(val role: String = Roles.ADMIN.name, val verified: Boolean = true)