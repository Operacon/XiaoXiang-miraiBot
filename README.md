## <div style="text-align:center">潇<b style="color:#39c5bb">小湘</b></div>

或许不烦的 mirai 机器人

----

`潇小湘` 是基于 [mirai](https://github.com/mamoe/mirai) 的 QQ 机器人。以 mirai 插件的形式开发和发布。

应 [mirai](https://github.com/mamoe/mirai) 倡议，本机器人采用 `AGPLv3` 协议开源。

#### 许可证

```
    XiaoXiang, May not be an annoying mirai bot
    Copyright (C) 2022 Operacon.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    To contact the author, E-Mail <operacon@outlook.com>
```

`潇小湘` 仅以学习和娱乐为目的进行开发。尝试对此类机器人进行一定架构，使得项目更加清晰，便于定制、添加功能和二次开发。同时为像作者一样基础一般的开发者服务。

----

### 项目结构

```
    kotlin
    |-------bean
    |       |-------项目中用到的全局单例、配置文件以及其他 bean
    |
    |-------controller
    |       |-------所有 event listener，listener 中不包含
    |               业务逻辑，只会调用其中 service 的 scan()
    |
    |-------service
    |       |-------所有 service 的业务逻辑，建议按服务对象分包
    |
    |-------XiaoXiang.kt
```

为便于理解，以处理一条群聊消息为例，给出处理流程：

```
    QQ 收到群聊消息，mirai 发出 GroupMessageEvent
        在 XiaoXiang.kt 中注册的 listener 类被实例化，monitor() 函数被调用
            作为 controller 的 listener 预处理消息文本
            listener 将预处理的文本发给其中第一个 service 的 scan() 函数
                scan() 发现不调用自己，return false
            listener 将预处理的文本发给其中第二个 service 的 scan() 函数
                scan() 发现不调用自己，return false
            ...
            listener 将预处理的文本发给其中第 n 个 service 的 scan() 函数
                scan() 发现调用自己，进行相应的业务逻辑的处理（处理中可能用到 bean），return true
            listener 发现本条消息处理完毕或者没有触发任何一个 service，return
        monitor() 函数结束
    event 处理结束    
```

----

### 已经开发的功能

#### 群聊

- hello 测试。发送 `小湘`，bot 若回复说明在线。
- 复读。bot 在侦测到两句相同的发言（即复读已经发生）时会复读。
- 求签。发送 `求签 <sentence>` 进行求签，其结果保留 30 min 不变。
- 概率。发送 `概率 <sentence>` 求解概率，其结果保留 30 min 不变。
- 决定。发送 `决定 <object0> <object1> ... <objectn>` 让 bot 帮选一项。
- 评价。发送 `评价一下 [any]` 让 bot 评价一下。
- 摆烂。发送 `摆烂额度` 查询额度，在任何 bot 存在的群里发言都会累加。发送 `睡大觉` 或者 `摆烂` 触发。可在配置文件中定制。

#### 私聊

#### 管理员

某些敏感的操作只有 bot 主人有权执行。以下命令仅对在配置文件中指定的 bot 主人私聊 bot 时有效。

- 管理员模式开关。键入 `sudo` 开启，`exit` 关闭。开关的口令可在配置文件中定制。

以下命令仅在管理员模式下有效：

- 热加载配置文件。mirai 重启会自动重新加载配置文件，但是可以通过键入 `reload` 或 `重载` 在不停机的情况下重新加载。
- 通过别名向群聊转发消息。在配置文件中配置群聊别名和群号，向 bot 私发群聊别名，下一条发给 bot 的消息被 bot 转发给目标群。
- 广播。可以向 bot 所在的全部群聊或者全部好友广播消息。通过发送 `group broadcast` 或 `群聊广播` ，以及 `friend broadcast` 或 `私聊广播` 控制。下一条发给 bot 的消息被广播。

#### 通用

- 聊天机器人。（详见配置文件）
    - 群聊中，键入以 `。` 开头的（可在配置文件中定制）的句子以和 bot 聊天。
    - 积极群。一切消息转发给 bot 以维持语境，但 bot 仅有给定概率回复。

- 定时任务。
    - 见 bean/scheduler.kt ，基于 Quartz 实现定时任务。
    - 每个单独的 Job 应在 bean/jobs/ 下。

----

### 配置文件

插件加载一次后，在 `config/org.operacon.xiaoXiang/` 下可以找到若干个配置文件。

配置文件中的注释应当详细描述了该设置项对应的功能。

----

### 开发提示

