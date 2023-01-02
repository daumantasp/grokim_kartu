package com.dauma.grokimkartu.repositories.profile.entities

class ProfileUnreadCount(
    var unreadNotificationsCount: Int?,
    var unreadPrivateConversationsCount: Int?,
    var unreadThomannConversationsCount: Int?
) {
    constructor(profileUnreadCount: ProfileUnreadCount) : this(
        unreadNotificationsCount = profileUnreadCount.unreadNotificationsCount,
        unreadPrivateConversationsCount = profileUnreadCount.unreadPrivateConversationsCount,
        unreadThomannConversationsCount = profileUnreadCount.unreadThomannConversationsCount
    )
}