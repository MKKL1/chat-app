# Permissions

## Main concepts
### Permissions
Represents actions which user can perform while using szampchat's system.

### Permission overwrites
Signalizes how permissions should be changed in different contexts.

To further explain this, if base permissions on community allow user to create message, each channel can independently change it, so that channel can be written to only by selected roles.

<details>
    <summary>Form example</summary>

    Imagine form like this for permission overwrite:
    |Permission|Deny|_|Allow|
    |-|-|-|-|
    |Allow sending messages| | |x|  
    |Allow reaction| |x| |  
    |Allow attachments| x | | |

    In this example, base permission could allow or deny reacting to messages, which would be respected by permission overwrite. However it would completely overwrite sending messages and attachments permissions.
</details>

### Permission scope
Each permission has defined scope of it's operation. For example, it makes no sense for `INVITE_CREATE` to be overwriten by channel's permission overwrites, as changing channels shouldn't allow/deny user permission to create invite links.

Permission scope should be taken into consideration in context of permission overwrites.

## Storage
### Permissions
Permissions are stored as 32 bit number, where each bit represents different permission. 

For example to set ROLE_MODIFY permission on bit 2, we can use binary operation such as:
```python
permission_data | (1 << 2)
```
or more generaly:
```python
def allow(permission):
    permission_data | (1 << permission.bit_number)
```
Same goes for denying:
```python
def deny(permission):
    permission_data & ~(1 << permission.bit_number)
```

#### Permission flags
| Bit number | Permission name | Scope | Explanation |
|-|-|-|-|
| 0 | ADMINISTRATOR | C |Special flag. If set, allows are other operations without having to specify them|
|1|ROLE_MODIFY|C|Creating/modifying roles (replace by admin flag?)|
|2|INVITE_CREATE|C||
|3|CHANNEL_CREATE|T||
|4|CHANNEL_MODIFY|T||
|5|MESSAGE_CREATE|T|Sending message in text channel|
|6|MESSAGE_DELETE|T|Removing any message in text channel|
|7|REACTION_CREATE|T|Adding reaction to message|
|8|...|...||

### Permission overwrite
Similary to Permissions, permission overwrites are stored as a 64 bit number. First 32 bits are used as an `allow` flags and last 32 bits are `deny` flags. 
|Bit number|Op|
|-|-|
|0-31| allow|
|32-63|deny|

It could be also understood as two `Permission` types stored in one type.

#### Example of channel overwrites
Base permission - default permissions of community

|Bit n|Base|Allow|Deny|Result|Comment|
|-|-|-|-|-|-|
|0|0|1|0|0|Admin flag cannot be overwriten in channel scope (See [#Permission Scope](#permission-scope))|
|3|0|1|0|1|`CHANNEL_CREATE` can be overwriten|
|4|0|0|0|0|It can also do nothing|
|5|1|0|0|1||
|6|1|0|1|0|Even if base permission allows `6` flag, overwrite can deny that|
|7|0|1|1|1|If both overwrites are set, then allow flag always has priority|

#### Binary operation example
To set allow `2` flag we can simply:
```
overwrite_data | (1 << 2)
```
To deny `2` flag, we have to shift it to the left by additional 32 positions (Remember about cast to long):
```
overwrite_data | (long)(1 << 2+32)
```

#### Applying overwrites example

permOverwrites - 64 bit type

basePermissions - 32 bit type

```python
def apply(permOverwrites, basePermissions):
    basePermissions |= int(permOverwrites)
    basePermissions &= ~(int(permOverwrites >> 32))
    return basePermissions

```